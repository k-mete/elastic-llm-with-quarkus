package org.dimsen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.dimsen.model.base.BaseEntity;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sub_categories")
@Getter
@Setter
@Indexed(index = "sub_categories")
public class SubCategory extends BaseEntity {
    
    @NotNull
    @Column(name = "name", nullable = false)
    @FullTextField(analyzer = "standard")
    private String name;
    
    @Column(name = "description")
    @FullTextField(analyzer = "standard")
    private String description;
    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @IndexedEmbedded
    private Category category;
    
    @OneToMany(mappedBy = "subCategory")
    private List<Asset> assets = new ArrayList<>();
} 