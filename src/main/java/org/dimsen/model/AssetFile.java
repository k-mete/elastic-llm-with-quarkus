package org.dimsen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.dimsen.model.base.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "asset_files")
@Getter
@Setter
public class AssetFile extends BaseEntity {
    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;
    
    @NotNull
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @NotNull
    @Column(name = "file_type", nullable = false)
    private String fileType;
    
    @NotNull
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @NotNull
    @Column(name = "file_size", nullable = false)
    private Long fileSize;
    
    @Column(name = "description")
    private String description;
    
    @NotNull
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;
    
    @NotNull
    @Column(name = "uploaded_by", nullable = false)
    private String uploadedBy;
} 