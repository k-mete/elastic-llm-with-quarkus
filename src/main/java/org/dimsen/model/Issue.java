package org.dimsen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.dimsen.model.base.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "issues")
@Getter
@Setter
public class Issue extends BaseEntity {
    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;
    
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;
    
    @NotNull
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @NotNull
    @Column(name = "priority", nullable = false)
    private String priority;  // HIGH, MEDIUM, LOW
    
    @NotNull
    @Column(name = "status", nullable = false)
    private String status;    // OPEN, IN_PROGRESS, RESOLVED, CLOSED
    
    @Column(name = "resolution", columnDefinition = "TEXT")
    private String resolution;
    
    @NotNull
    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt;
    
    @NotNull
    @Column(name = "reported_by", nullable = false)
    private String reportedBy;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @Column(name = "resolved_by")
    private String resolvedBy;
} 