package org.dimsen.model.base;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.dimsen.constant.AppConstant;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base entity class that provides common fields and functionality for all entities.
 * This class implements common audit fields, soft delete, and optimistic locking.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GenericField
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @NotNull
    @Column(name = "uuid", unique = true, updatable = false)
    private String uuid = UUID.randomUUID().toString();

    @NotNull
    @Column(name = "tenant_id")
    private String tenantId = AppConstant.DEFAULT_TENANT;

    @NotNull
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @NotNull
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.createdBy = AppConstant.SYSTEM;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = AppConstant.SYSTEM;
        this.isActive = true;
        this.isDeleted = false;
        
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
        
        if (this.tenantId == null) {
            this.tenantId = AppConstant.DEFAULT_TENANT;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = AppConstant.SYSTEM;
    }

    /**
     * Soft deletes the entity by marking it as deleted and inactive
     * @param deletedBy The user or system that performed the deletion
     */
    public void softDelete(String deletedBy) {
        this.isDeleted = true;
        this.isActive = false;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    /**
     * Restores a soft-deleted entity by marking it as not deleted and active
     */
    public void restore() {
        this.isDeleted = false;
        this.isActive = true;
        this.deletedAt = null;
        this.deletedBy = null;
    }
}
