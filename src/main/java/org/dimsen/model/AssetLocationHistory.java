package org.dimsen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.dimsen.model.base.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "asset_location_history")
@Getter
@Setter
public class AssetLocationHistory extends BaseEntity {
    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;
    
    @NotNull
    @Column(name = "location", nullable = false)
    private String location;
    
    @NotNull
    @Column(name = "building", nullable = false)
    private String building;
    
    @NotNull
    @Column(name = "floor", nullable = false)
    private String floor;
    
    @NotNull
    @Column(name = "room", nullable = false)
    private String room;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @NotNull
    @Column(name = "moved_at", nullable = false)
    private LocalDateTime movedAt;
    
    @NotNull
    @Column(name = "moved_by", nullable = false)
    private String movedBy;
} 