package org.dimsen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.dimsen.model.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "types")
@Getter
@Setter
public class Type extends BaseEntity {
    
    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @OneToMany(mappedBy = "type")
    private List<Asset> assets = new ArrayList<>();
} 