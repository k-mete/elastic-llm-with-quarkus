package org.dimsen.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AssetSearchDTO(
    String id,
    String name,
    String serialNumber,
    String brandName,
    String brandDescription,
    String categoryName,
    String typeName,
    String subCategoryName,
    LocalDateTime purchaseDate,
    Double purchasePrice,
    String description,
    String status,
    String llmCommentary
) {} 