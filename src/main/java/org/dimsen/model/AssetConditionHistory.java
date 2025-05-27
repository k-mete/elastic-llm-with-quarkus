package org.dimsen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.dimsen.model.base.BaseEntity;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "asset_condition_history")
@Getter
@Setter
@Indexed(index = "asset_condition_history")
public class AssetConditionHistory extends BaseEntity {
    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;
    
    @NotNull
    @Column(name = "condition", nullable = false)
    @KeywordField
    private String condition;
    
    @NotNull
    @Column(name = "notes", nullable = false, columnDefinition = "TEXT")
    @FullTextField(analyzer = "standard")
    private String notes;
    
    @NotNull
    @Column(name = "recorded_at", nullable = false)
    @GenericField
    private LocalDateTime recordedAt;
    
    @NotNull
    @Column(name = "recorded_by", nullable = false)
    @KeywordField
    private String recordedBy;
} 