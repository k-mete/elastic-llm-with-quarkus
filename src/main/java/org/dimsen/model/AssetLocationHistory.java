package org.dimsen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.dimsen.model.base.BaseEntity;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "asset_location_history")
@Getter
@Setter
@Indexed(index = "asset_location_history")
public class AssetLocationHistory extends BaseEntity {
    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;
    
    @NotNull
    @Column(name = "location", nullable = false)
    @KeywordField
    private String location;
    
    @NotNull
    @Column(name = "building", nullable = false)
    @KeywordField
    private String building;
    
    @NotNull
    @Column(name = "floor", nullable = false)
    @KeywordField
    private String floor;
    
    @NotNull
    @Column(name = "room", nullable = false)
    @KeywordField
    private String room;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    @FullTextField(analyzer = "standard")
    private String notes;
    
    @NotNull
    @Column(name = "moved_at", nullable = false)
    @GenericField
    private LocalDateTime movedAt;
    
    @NotNull
    @Column(name = "moved_by", nullable = false)
    @KeywordField
    private String movedBy;
} 