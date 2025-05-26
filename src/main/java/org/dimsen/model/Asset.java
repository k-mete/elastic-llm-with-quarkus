package org.dimsen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.dimsen.model.base.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "assets")
public class Asset extends BaseEntity {
    
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;
    
    @NotNull
    @Column(name = "serial_number", nullable = false)
    private String serialNumber;
    
    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;
    
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private Type type;
    
    @ManyToOne
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;
    
    @NotNull
    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;
    
    @NotNull
    @Column(name = "purchase_price", nullable = false)
    private Double purchasePrice;
    
    @Column(name = "description")
    private String description;
    
    @NotNull
    @Column(name = "status", nullable = false)
    private String status;
    
    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL)
    private List<AssetConditionHistory> conditionHistory = new ArrayList<>();
    
    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL)
    private List<AssetLocationHistory> locationHistory = new ArrayList<>();
    
    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL)
    private List<AssetFile> files = new ArrayList<>();
    
    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL)
    private List<Issue> issues = new ArrayList<>();
    
    @OneToOne(mappedBy = "asset", cascade = CascadeType.ALL)
    private DisposedAsset disposedAsset;
} 