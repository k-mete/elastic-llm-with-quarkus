package org.dimsen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.dimsen.enums.Actions;
import org.dimsen.model.base.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_trails")
@Getter
@Setter
public class AuditTrail extends BaseEntity {
    
    @NotNull
    @Column(name = "entity_type", nullable = false)
    private String entityType;
    
    @NotNull
    @Column(name = "entity_id", nullable = false)
    private Long entityId;
    
    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "action", nullable = false)
    private Actions action;
    
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;
    
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;
    
    @NotNull
    @Column(name = "action_at", nullable = false)
    private LocalDateTime actionAt;
    
    @NotNull
    @Column(name = "action_by", nullable = false)
    private String actionBy;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
} 