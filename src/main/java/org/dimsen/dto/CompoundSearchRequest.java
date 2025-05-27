package org.dimsen.dto;

import java.util.Map;
 
public record CompoundSearchRequest(
    String naturalLanguageQuery,
    Map<String, String> filters,
    Integer limit
) {} 