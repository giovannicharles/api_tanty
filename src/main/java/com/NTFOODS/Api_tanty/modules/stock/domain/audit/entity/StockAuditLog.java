package com.NTFOODS.Api_tanty.modules.stock.domain.audit.entity;

import java.time.LocalDateTime;

/**
 * StockAuditLog - Trace d'audit des actions sur le stock.
 */
public class StockAuditLog {

    private Long id;
    private String entityType; // STOCK_ITEM, BATCH, MOVEMENT, ALERT, etc.
    private String entityId;
    private String action; // CREATE, UPDATE, DELETE, VALIDATE, TRANSFER, etc.
    private String userMatricule;
    private String userName;
    private String oldValues; // JSON snapshot
    private String newValues; // JSON snapshot
    private String reason;
    private LocalDateTime timestamp;

    public StockAuditLog(String entityType, String entityId, String action,
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
