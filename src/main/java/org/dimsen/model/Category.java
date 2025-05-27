package org.dimsen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.dimsen.model.base.BaseEntity;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@Indexed(index = "categories")
public class Category extends BaseEntity {
    
    @NotNull
    @Column(nullable = false, unique = true)
    @FullTextField(analyzer = "standard")
    private String name;
    
    @FullTextField(analyzer = "standard")
    private String description;
    
    @OneToMany(mappedBy = "category")
    private List<Asset> assets = new ArrayList<>();
    
    @OneToMany(mappedBy = "category")
    private List<SubCategory> subCategories = new ArrayList<>();
} 