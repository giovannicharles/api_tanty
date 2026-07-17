package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.audit.jpa;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_audit_logs", indexes = {
        @Index(name = "idx_audit_entity", columnList = "entity_type, entity_id"),
        @Index(name = "idx_audit_user", columnList = "user_matricule"),
        @Index(name = "idx_audit_timestamp", columnList = "timestamp")
})
public class StockAuditLogJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private String entityId;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "user_matricule")
    private String userMatricule;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "old_values", columnDefinition = "TEXT")
    private String oldValues;

    @Column(name = "new_values", columnDefinition = "TEXT")
    private String newValues;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public StockAuditLogJpaEntity() {}

    public StockAuditLogJpaEntity(String entityType, String entityId, String action,
                                    String userMatricule, String userName,
                                    String oldValues, String newValues, String reason) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.action = action;
        this.userMatricule = userMatricule;
        this.userName = userName;
        this.oldValues = oldValues;
        this.newValues = newValues;
        this.reason = reason;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getUserMatricule() { return userMatricule; }
    public void setUserMatricule(String userMatricule) { this.userMatricule = userMatricule; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getOldValues() { return oldValues; }
    public void setOldValues(String oldValues) { this.oldValues = oldValues; }

    public String getNewValues() { return newValues; }
    public void setNewValues(String newValues) { this.newValues = newValues; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
