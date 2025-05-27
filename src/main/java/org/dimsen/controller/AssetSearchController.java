package org.dimsen.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.dimsen.dto.CompoundSearchRequest;
import org.dimsen.dto.AssetIssueSearchRequest;
import org.dimsen.service.AssetSearchService;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@Path("/assets/search")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class AssetSearchController {

    private static final Logger LOG = Logger.getLogger(AssetSearchController.class);

    @Inject
    AssetSearchService searchService;

    @GET
    @Path("/fuzzy-name")
    public Response fuzzyNameSearch(
            @QueryParam("name") String nameQuery,
            @QueryParam("maxEdits") Integer maxEdits,
            @QueryParam("limit") @DefaultValue("20") int limit) {
        
        LOG.infof("Received fuzzy name search request - name='%s', maxEdits=%s, limit=%d",
                  nameQuery, maxEdits != null ? maxEdits : "default", limit);

        if (nameQuery == null || nameQuery.isBlank()) {
            LOG.warn("Fuzzy name search request rejected: missing or empty name parameter");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Name query parameter is required")
                    .build();
        }

        try {
            var startTime = System.currentTimeMillis();
            var results = searchService.fuzzyNameSearch(nameQuery, maxEdits, limit);
            var duration = System.currentTimeMillis() - startTime;

            LOG.infof("Fuzzy name search request completed in %d ms, returning %d results",
                      duration, results.size());

            return Response.ok(results).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error processing fuzzy name search - name='%s': %s",
                       nameQuery, e.getMessage());
            return Response.serverError()
                    .entity("Error processing search request: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/multi-field")
    public Response multiFieldSearch(
            @QueryParam("q") String searchTerm,
            @QueryParam("limit") @DefaultValue("20") int limit) {
        
        LOG.infof("Received multi-field search request - term='%s', limit=%d",
                  searchTerm, limit);

        if (searchTerm == null || searchTerm.isBlank()) {
            LOG.warn("Multi-field search request rejected: missing or empty search term");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Search term parameter is required")
                    .build();
        }

        try {
            var startTime = System.currentTimeMillis();
            var results = searchService.multiFieldSearch(searchTerm, limit);
            var duration = System.currentTimeMillis() - startTime;

            LOG.infof("Multi-field search request completed in %d ms, returning %d results",
                      duration, results.size());

            return Response.ok(results).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error processing multi-field search - term='%s': %s",
                       searchTerm, e.getMessage());
            return Response.serverError()
                    .entity("Error processing search request: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/reindex")
    public Response reindexAll() {
        LOG.info("Received reindex request");
        
        try {
            var startTime = System.currentTimeMillis();
            searchService.reindexAll();
            var duration = System.currentTimeMillis() - startTime;
            
            LOG.infof("Reindex completed successfully in %d ms", duration);
            return Response.ok("Reindexing completed successfully").build();
        } catch (InterruptedException e) {
            LOG.error("Reindex operation failed", e);
            return Response.serverError()
                    .entity("Reindexing failed: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/compound")
    public Response compoundSearch(CompoundSearchRequest request) {
        LOG.infof("Received compound search request with query: %s", 
                  request.naturalLanguageQuery());

        if (request.naturalLanguageQuery() == null || request.naturalLanguageQuery().isBlank()) {
            LOG.warn("Compound search request rejected: missing or empty query");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Natural language query is required")
                    .build();
        }

        try {
            var startTime = System.currentTimeMillis();
            var results = searchService.compoundSearch(request);
            var duration = System.currentTimeMillis() - startTime;

            LOG.infof("Compound search request completed in %d ms, returning %d results",
                      duration, results.size());

            return Response.ok(results).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error processing compound search - query='%s': %s",
                       request.naturalLanguageQuery(), e.getMessage());
            return Response.serverError()
                    .entity("Error processing search request: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/issues")
    public Response searchByIssues(AssetIssueSearchRequest request) {
        LOG.infof("Received issue search request: %s", request);

        try {
            var startTime = System.currentTimeMillis();
            
            // Validate request
            if (request.issueSearchCriteria() == null) {
                LOG.warn("Issue search request rejected: missing issue search criteria");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Issue search criteria is required")
                        .build();
            }

            // If LLM enhancement is requested, validate natural language query
            if (request.enhanceWithLLM() && (request.naturalLanguageQuery() == null || request.naturalLanguageQuery().isBlank())) {
                LOG.warn("Issue search request rejected: LLM enhancement requested but no natural language query provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Natural language query is required when LLM enhancement is enabled")
                        .build();
            }

            var results = searchService.searchByIssues(request);
            var duration = System.currentTimeMillis() - startTime;

            if (results == null) {
                LOG.error("Search service returned null results");
                return Response.serverError()
                        .entity("Error processing search request")
                        .build();
            }

            LOG.infof("Issue search request completed in %d ms, returning %d results",
                      duration, results.size());

            return Response.ok(results).build();

        } catch (Exception e) {
            LOG.errorf(e, "Error processing issue search request: %s", e.getMessage());
            return Response.serverError()
                    .entity("Error processing search request: " + e.getMessage())
                    .build();
        }
    }
} 