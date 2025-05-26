package org.dimsen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.dimsen.model.base.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "disposed_assets")
@Getter
@Setter
public class DisposedAsset extends BaseEntity {
    
    @NotNull
    @OneToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;
    
    @NotNull
    @Column(name = "disposal_method", nullable = false)
    private String disposalMethod; // SOLD, DONATED, SCRAPPED, RECYCLED
    
    @NotNull
    @Column(name = "disposal_reason", nullable = false, columnDefinition = "TEXT")
    private String disposalReason;
    
    @Column(name = "disposal_value")
    private Double disposalValue;
    
    @Column(name = "disposal_recipient")
    private String disposalRecipient;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @NotNull
    @Column(name = "disposed_at", nullable = false)
    private LocalDateTime disposedAt;
    
    @NotNull
    @Column(name = "disposed_by", nullable = false)
    private String disposedBy;
} 