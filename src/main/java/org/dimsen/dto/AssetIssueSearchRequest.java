package org.dimsen.dto;

import java.util.Map;

public record AssetIssueSearchRequest(
    String naturalLanguageQuery,
    Map<String, String> filters,
    IssueSearchCriteria issueSearchCriteria,
    boolean enhanceWithLLM,
    Integer limit
) {
    public enum IssuePriority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public record IssueSearchCriteria(
            Boolean hasOpenIssues,          // For finding assets with any open issues
            Integer minIssueCount,          // For finding assets with X or more issues
            IssuePriority minPriority,      // For finding assets with issues at or above this priority
            Boolean includeResolved         // Whether to include resolved issues in the count
    ) {}
} 