package org.dimsen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.dimsen.model.base.BaseEntity;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "assets")
@Indexed(index = "assets")
public class Asset extends BaseEntity {
    
    @NotNull
    @Column(name = "name", nullable = false)
    @FullTextField(analyzer = "standard")
    private String name;
    
    @NotNull
    @Column(name = "serial_number", nullable = false)
    @KeywordField
    private String serialNumber;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id", nullable = false)
    @IndexedEmbedded(includeDepth = 1)
    private Brand brand;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @IndexedEmbedded(includeDepth = 1)
    private Category category;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id", nullable = false)
    @IndexedEmbedded(includeDepth = 1)
    private Type type;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sub_category_id")
    @IndexedEmbedded(includeDepth = 1)
    private SubCategory subCategory;
    
    @NotNull
    @Column(name = "purchase_date", nullable = false)
    @GenericField
    private LocalDateTime purchaseDate;
    
    @NotNull
    @Column(name = "purchase_price", nullable = false)
    @GenericField
    private Double purchasePrice;
    
    @Column(name = "description")
    @FullTextField(analyzer = "standard")
    private String description;
    
    @NotNull
    @Column(name = "status", nullable = false)
    @KeywordField
    private String status;
    
    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL)
    @IndexedEmbedded(includePaths = {"condition", "recordedAt"})
    private List<AssetConditionHistory> conditionHistory = new ArrayList<>();
    
    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL)
    @IndexedEmbedded(includePaths = {"location", "building", "floor", "room", "movedAt"})
    private List<AssetLocationHistory> locationHistory = new ArrayList<>();
    
    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL)
    private List<AssetFile> files = new ArrayList<>();
    
    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL)
    @IndexedEmbedded(includePaths = {"description", "priority", "reportedAt", "reportedBy", "resolution", "resolvedAt", "resolvedBy", "status", "title"})
    private List<Issue> issues = new ArrayList<>();
    
    @OneToOne(mappedBy = "asset", cascade = CascadeType.ALL)
    private DisposedAsset disposedAsset;
} 