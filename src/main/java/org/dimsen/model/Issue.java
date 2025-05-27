package org.dimsen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.dimsen.model.base.BaseEntity;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import java.time.LocalDateTime;

@Entity
@Table(name = "issues")
@Getter
@Setter
@Indexed(index = "issues")
public class Issue extends BaseEntity {
    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;
    
    @NotNull
    @Column(name = "title", nullable = false)
    @FullTextField(analyzer = "standard")
    private String title;
    
    @NotNull
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    @FullTextField(analyzer = "standard")
    private String description;
    
    @NotNull
    @Column(name = "priority", nullable = false)
    @KeywordField
    private String priority;  // HIGH, MEDIUM, LOW
    
    @NotNull
    @Column(name = "status", nullable = false)
    @KeywordField
    private String status;    // OPEN, IN_PROGRESS, RESOLVED, CLOSED
    
    @Column(name = "resolution", columnDefinition = "TEXT")
    @KeywordField
    private String resolution;
    
    @NotNull
    @Column(name = "reported_at", nullable = false)
    @GenericField
    private LocalDateTime reportedAt;
    
    @NotNull
    @Column(name = "reported_by", nullable = false)
    @GenericField
    private String reportedBy;
    
    @Column(name = "resolved_at")
    @GenericField
    private LocalDateTime resolvedAt;
    
    @Column(name = "resolved_by")
    @GenericField
    private String resolvedBy;
} 