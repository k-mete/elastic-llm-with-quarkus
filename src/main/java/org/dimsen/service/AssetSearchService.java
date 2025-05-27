package org.dimsen.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.dimsen.dto.AssetSearchDTO;
import org.dimsen.dto.CompoundSearchRequest;
import org.dimsen.dto.AssetIssueSearchRequest;
import org.dimsen.model.Asset;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class AssetSearchService {

    private static final Logger LOG = Logger.getLogger(AssetSearchService.class);

    @Inject
    EntityManager em;

    @Inject
    AssetSearchBot searchBot;

    /**
     * Performs a fuzzy search on asset names with typo tolerance
     * @param nameQuery The approximate name to search for
     * @param maxEdits Maximum number of character edits allowed (default: 2)
     * @param limit Maximum number of results to return
     * @return List of assets with similar names
     */
    public List<AssetSearchDTO> fuzzyNameSearch(String nameQuery, Integer maxEdits, int limit) {
        LOG.infof("Starting fuzzy name search with query='%s', maxEdits=%d, limit=%d", 
                  nameQuery, maxEdits != null ? maxEdits : 2, limit);
        
        SearchSession searchSession = Search.session(em);
        
        var startTime = System.currentTimeMillis();
        var results = searchSession.search(Asset.class)
                .where(f -> f.match()
                        .field("name")
                        .matching(nameQuery)
                        .fuzzy(maxEdits != null ? maxEdits : 2))
                .sort(f -> f.score())
                .fetchHits(limit);
        
        var duration = System.currentTimeMillis() - startTime;
        LOG.infof("Fuzzy name search completed in %d ms, found %d results", 
                  duration, results.size());
        
        return results.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Performs a full-text search across multiple fields (name, description, and brand.name)
     * @param searchTerm The term to search for across fields
     * @param limit Maximum number of results to return
     * @return List of assets matching the search term
     */
    public List<AssetSearchDTO> multiFieldSearch(String searchTerm, int limit) {
        LOG.infof("Starting multi-field search with term='%s', limit=%d", searchTerm, limit);
        
        SearchSession searchSession = Search.session(em);
        
        var startTime = System.currentTimeMillis();
        var result = searchSession.search(Asset.class)
                .where(f -> f.bool()
                        .should(f.match()
                                .field("name").boost(2.0f)
                                .field("description")
                                .field("brand.name")
                                .field("brand.description")
                                .matching(searchTerm))
                        .should(f.phrase()
                                .field("name").boost(3.0f)
                                .field("description")
                                .field("brand.name")
                                .field("brand.description")
                                .matching(searchTerm)))
                .sort(f -> f.score())
                .fetchHits(limit);
        
        var duration = System.currentTimeMillis() - startTime;
        LOG.infof("Multi-field search completed in %d ms, found %d results", 
                  duration, result.size());
        
        return result.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private String cleanupJsonResponse(String response) {
        LOG.debugf("Cleaning up JSON response: '%s'", response);
        
        // Remove any leading/trailing whitespace
        String cleaned = response.trim();
        
        // Find the first '{' and last '}'
        int start = cleaned.indexOf("{");
        int end = cleaned.lastIndexOf("}");
        
        if (start == -1 || end == -1 || end <= start) {
            LOG.error("Invalid JSON structure - missing braces");
            throw new IllegalStateException("Invalid JSON response from bot");
        }
        
        // Extract just the JSON object
        cleaned = cleaned.substring(start, end + 1);
        LOG.debugf("Cleaned JSON: '%s'", cleaned);
        
        // Validate basic JSON structure
        if (!cleaned.startsWith("{") || !cleaned.endsWith("}")) {
            LOG.error("JSON cleanup failed - invalid structure");
            throw new IllegalStateException("Invalid JSON structure after cleanup");
        }
        
        return cleaned;
    }

    /**
     * Performs a compound search with multiple filters and natural language processing
     */
    public List<AssetSearchDTO> compoundSearch(CompoundSearchRequest request) {
        LOG.infof("Starting compound search with query='%s', filters=%s", 
                  request.naturalLanguageQuery(), 
                  request.filters());
        
        // First, perform the database search with explicit filters
        SearchSession searchSession = Search.session(em);
        List<AssetSearchDTO> searchResults;
        
        try {
            var startTime = System.currentTimeMillis();
            
            // Debug: Log the current index status
            LOG.info("Checking Elasticsearch index status...");
            var indexer = searchSession.massIndexer(Asset.class);
            indexer.idFetchSize(150)
                  .batchSizeToLoadObjects(25)
                  .threadsToLoadObjects(12)
                  .startAndWait();
            LOG.info("Index check/update completed");
            
            // Execute the search
            var results = searchSession.search(Asset.class)
                    .where(f -> createSearchPredicate(f, request.filters()))
                    .sort(f -> f.score())
                    .fetchHits(request.limit() != null ? request.limit() : 20);
            
            var duration = System.currentTimeMillis() - startTime;
            LOG.infof("Database search completed in %d ms, found %d results", duration, results.size());
            
            if (results.isEmpty()) {
                LOG.info("No results found. Current filters:");
                if (request.filters() != null) {
                    request.filters().forEach((key, value) -> 
                        LOG.infof("  %s: '%s'", key, value));
                }
                
                // Debug: Try to fetch all assets to verify database connection
                var allAssets = searchSession.search(Asset.class)
                        .where(f -> f.matchAll())
                        .fetchHits(5);
                LOG.infof("Debug: Found %d total assets in index", allAssets.size());
                if (!allAssets.isEmpty()) {
                    LOG.info("Sample asset from index:");
                    var sample = allAssets.get(0);
                    LOG.infof("  Name: %s", sample.getName());
                    LOG.infof("  Brand: %s", sample.getBrand() != null ? sample.getBrand().getName() : "null");
                    LOG.infof("  Type: %s", sample.getType() != null ? sample.getType().getName() : "null");
                    LOG.infof("  Status: %s", sample.getStatus());
                }
            }
            
            // Convert results to DTOs
            searchResults = results.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());

            // If we have a natural language query and results, enhance them with LLM
            if (!searchResults.isEmpty() && request.naturalLanguageQuery() != null && !request.naturalLanguageQuery().isBlank()) {
                try {
                    enhanceSearchResults(searchResults, request.naturalLanguageQuery());
                } catch (Exception e) {
                    LOG.error("Failed to enhance search results with LLM", e);
                }
            }
            
            return searchResults;
            
        } catch (Exception e) {
            LOG.errorf("Error executing search: %s", e.getMessage(), e);
            return null;
        }
    }

    private void enhanceSearchResults(List<AssetSearchDTO> results, String query) {
        try {
            for (int i = 0; i < results.size(); i++) {
                var asset = results.get(i);
                
                // Format context for LLM analysis
                StringBuilder context = new StringBuilder();
                context.append("Analyze this IT asset:\n\n");
                context.append("Name: ").append(asset.name()).append("\n");
                context.append("Brand: ").append(asset.brandName()).append("\n");
                context.append("Type: ").append(asset.typeName()).append("\n");
                context.append("Category: ").append(asset.categoryName()).append("\n");
                context.append("SubCategory: ").append(asset.subCategoryName()).append("\n");
                context.append("Status: ").append(asset.status()).append("\n");
                context.append("Description: ").append(asset.description()).append("\n");
                context.append("Purchase Price: $").append(asset.purchasePrice()).append("\n");
                context.append("Purchase Date: ").append(asset.purchaseDate()).append("\n");
                
                // Get LLM analysis
                String analysis = searchBot.analyzeSearchResults(context.toString());
                LOG.debugf("Generated commentary for asset %s: %s", asset.name(), analysis);
                
                // Create new DTO with the commentary
                results.set(i, new AssetSearchDTO(
                    asset.id(),
                    asset.name(),
                    asset.serialNumber(),
                    asset.brandName(),
                    asset.brandDescription(),
                    asset.categoryName(),
                    asset.typeName(),
                    asset.subCategoryName(),
                    asset.purchaseDate(),
                    asset.purchasePrice(),
                    asset.description(),
                    asset.status(),
                    analysis
                ));
            }
        } catch (Exception e) {
            LOG.error("Error enhancing search results with LLM analysis", e);
        }
    }

    private org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep createSearchPredicate(
            SearchPredicateFactory f, Map<String, String> filters) {
        
        LOG.infof("Creating search predicate with filters: %s", filters);
        var bool = f.bool();

        if (filters != null) {
            filters.forEach((key, value) -> {
                if (value != null && !value.isBlank()) {
                    switch (key.toLowerCase()) {
                        case "brand" -> {
                            LOG.debugf("Adding brand filter: %s", value);
                            bool.must(f.match()
                                    .field("brand.name")
                                    .matching(value)
                                    .fuzzy(1));
                        }
                        case "type" -> {
                            LOG.debugf("Adding type filter: %s", value);
                            bool.must(f.match()
                                    .field("type.name")
                                    .matching(value)
                                    .fuzzy(1));
                        }
                        case "status" -> {
                            LOG.debugf("Adding status filter: %s", value);
                            bool.must(f.match()
                                    .field("status")
                                    .matching(value));
                        }
                        case "condition" -> {
                            LOG.debugf("Adding condition filter: %s", value);
                            bool.must(f.match()
                                    .field("conditionHistory.condition")
                                    .matching(value));
                        }
                        default -> LOG.warnf("Ignoring unknown filter key: %s", key);
                    }
                }
            });
        }

        return bool;
    }

    private AssetSearchDTO mapToDTO(Asset asset) {
        return new AssetSearchDTO(
            asset.getId().toString(),
            asset.getName(),
            asset.getSerialNumber(),
            asset.getBrand() != null ? asset.getBrand().getName() : null,
            asset.getBrand() != null ? asset.getBrand().getDescription() : null,
            asset.getCategory() != null ? asset.getCategory().getName() : null,
            asset.getType() != null ? asset.getType().getName() : null,
            asset.getSubCategory() != null ? asset.getSubCategory().getName() : null,
            asset.getPurchaseDate(),
            asset.getPurchasePrice(),
            asset.getDescription(),
            asset.getStatus(),
            null  // LLM commentary will be added later
        );
    }

    public void reindexAll() throws InterruptedException {
        LOG.info("Starting full reindexing of assets");
        var startTime = System.currentTimeMillis();
        
        SearchSession searchSession = Search.session(em);
        searchSession.massIndexer()
                .threadsToLoadObjects(4)
                .batchSizeToLoadObjects(50)
                .idFetchSize(150)
                .startAndWait();
        
        var duration = System.currentTimeMillis() - startTime;
        LOG.infof("Reindexing completed in %d ms", duration);
    }

    /**
     * Search for assets based on their issues (open issues, issue count, priority)
     */
    public List<AssetSearchDTO> searchByIssues(AssetIssueSearchRequest request) {
        LOG.infof("Starting issue-based search with criteria: %s", request.issueSearchCriteria());
        
        SearchSession searchSession = Search.session(em);
        List<AssetSearchDTO> searchResults;
        
        try {
            var startTime = System.currentTimeMillis();
            
            // Build the search predicate
            var searchQuery = searchSession.search(Asset.class)
                    .where(f -> {
                        var bool = f.bool();
                        
                        // Add basic filters if present
                        if (request.filters() != null && !request.filters().isEmpty()) {
                            bool.must(createSearchPredicate(f, request.filters()));
                        }
                        
                        // Add issue-specific predicates
                        addIssuePredicates(f, bool, request.issueSearchCriteria());
                        
                        return bool;
                    });
            
            // Execute search
            var results = searchQuery
                    .sort(f -> f.score())
                    .fetchHits(request.limit() != null ? request.limit() : 20);
            
            var duration = System.currentTimeMillis() - startTime;
            LOG.infof("Issue search completed in %d ms, found %d results", duration, results.size());
            
            // Convert to DTOs
            searchResults = results.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());

            // Enhance with LLM if requested and we have results
            if (request.enhanceWithLLM() && !searchResults.isEmpty() && 
                request.naturalLanguageQuery() != null && !request.naturalLanguageQuery().isBlank()) {
                try {
                    enhanceIssueSearchResults(searchResults, request);
                } catch (Exception e) {
                    LOG.error("Failed to enhance issue search results with LLM", e);
                }
            }
            
            return searchResults;
            
        } catch (Exception e) {
            LOG.errorf("Error executing issue search: %s", e.getMessage(), e);
            return null;
        }
    }

    private void addIssuePredicates(
            SearchPredicateFactory f, 
            org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep bool,
            AssetIssueSearchRequest.IssueSearchCriteria criteria) {
        
        if (criteria == null) return;

        if (Boolean.TRUE.equals(criteria.hasOpenIssues())) {
            LOG.debug("Adding open issues predicate");
            bool.must(f.match()
                    .field("issues.status")
                    .matching("OPEN"));
        }

        if (criteria.minIssueCount() != null && criteria.minIssueCount() > 0) {
            LOG.debugf("Adding minimum issue count predicate: %d", criteria.minIssueCount());
            // Use exists() to count issues
            bool.must(f.bool()
                    .should(f.exists()
                            .field("issues"))
                    .minimumShouldMatchNumber(criteria.minIssueCount()));
        }

        if (criteria.minPriority() != null) {
            LOG.debugf("Adding minimum priority predicate: %s", criteria.minPriority());
            bool.must(f.range()
                    .field("issues.priority")
                    .atLeast(criteria.minPriority().toString()));
        }

        // If we should include resolved issues
        if (!Boolean.TRUE.equals(criteria.includeResolved())) {
            bool.mustNot(f.match()
                    .field("issues.status")
                    .matching("RESOLVED"));
        }
    }

    private void enhanceIssueSearchResults(List<AssetSearchDTO> results, AssetIssueSearchRequest request) {
        try {
            for (int i = 0; i < results.size(); i++) {
                var asset = results.get(i);
                
                // Simplified context focusing on key details
                String context = String.format("""
                    Asset: %s (%s)
                    Type: %s
                    Status: %s
                    Issue Criteria:
                    - Open Issues: %s
                    - Min Issues: %s
                    - Min Priority: %s
                    """,
                    asset.name(),
                    asset.serialNumber(),
                    asset.typeName(),
                    asset.status(),
                    request.issueSearchCriteria().hasOpenIssues(),
                    request.issueSearchCriteria().minIssueCount(),
                    request.issueSearchCriteria().minPriority()
                );
                
                // Get LLM analysis
                String analysis = searchBot.analyzeIssueSearchResults(context);
                LOG.debugf("Generated issue commentary for asset %s: %s", asset.name(), analysis);
                
                // Create new DTO with the commentary
                results.set(i, new AssetSearchDTO(
                    asset.id(),
                    asset.name(),
                    asset.serialNumber(),
                    asset.brandName(),
                    asset.brandDescription(),
                    asset.categoryName(),
                    asset.typeName(),
                    asset.subCategoryName(),
                    asset.purchaseDate(),
                    asset.purchasePrice(),
                    asset.description(),
                    asset.status(),
                    analysis
                ));
            }
        } catch (Exception e) {
            LOG.error("Error enhancing issue search results with LLM analysis", e);
        }
    }
} 