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
@Table(name = "brands")
@Getter
@Setter
@Indexed(index = "brands")
public class Brand extends BaseEntity {
    
    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    @FullTextField(analyzer = "standard")
    private String name;
    
    @Column(name = "description")
    @FullTextField(analyzer = "standard")
    private String description;
    
    @OneToMany(mappedBy = "brand")
    private List<Asset> assets = new ArrayList<>();
} 