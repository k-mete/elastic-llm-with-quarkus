package org.dimsen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.dimsen.model.base.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "asset_condition_history")
@Getter
@Setter
public class AssetConditionHistory extends BaseEntity {
    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;
    
    @NotNull
    @Column(name = "condition", nullable = false)
    private String condition;
    
    @NotNull
    @Column(name = "notes", nullable = false, columnDefinition = "TEXT")
    private String notes;
    
    @NotNull
    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;
    
    @NotNull
    @Column(name = "recorded_by", nullable = false)
    private String recordedBy;
} 