package com.NTFOODS.Api_tanty.modules.stock.domain.alerte.entity;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * StockAlert - Entité représentant une alerte de stock
 * Niveaux de priorité: CRITICAL, HIGH, MEDIUM, LOW
 */
public class StockAlert {
    
    private Long id;
    private final AlertType type;
    private final AlertPriority priority;
    private final StockLocationId locationId;
    private final Long productId;
    private final String productSku;
    private final String productName;
    private final BigDecimal currentQuantity;
    private final BigDecimal threshold;
    private String message;
    private boolean acknowledged;
    private UserId acknowledgedBy;
    private LocalDateTime acknowledgedAt;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private String status; // ACTIVE, ACKNOWLEDGED, RESOLVED
    
    public StockAlert(AlertType type, AlertPriority priority, StockLocationId locationId,
                     Long productId, String productSku, String productName,
                     BigDecimal currentQuantity, BigDecimal threshold, String message) {
        this.type = type;
        this.priority = priority;
        this.locationId = locationId;
        this.productId = productId;
        this.productSku = productSku;
        this.productName = productName;
        this.currentQuantity = currentQuantity;
        this.threshold = threshold;
        this.message = message;
        this.acknowledged = false;
        this.createdAt = LocalDateTime.now();
        this.status = "ACTIVE";
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public AlertType getType() {
        return type;
    }
    
    public AlertPriority getPriority() {
        return priority;
    }
    
    public StockLocationId getLocationId() {
        return locationId;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public String getProductSku() {
        return productSku;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public BigDecimal getCurrentQuantity() {
        return currentQuantity;
    }
    
    public BigDecimal getThreshold() {
        return threshold;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isAcknowledged() {
        return acknowledged;
    }
    
    public UserId getAcknowledgedBy() {
        return acknowledgedBy;
    }
    
    public LocalDateTime getAcknowledgedAt() {
        return acknowledgedAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    
    public String getStatus() {
        return status;
    }
    
    /**
     * Acknowledge l'alerte
     */
    public void acknowledge(UserId userId) {
        this.acknowledged = true;
        this.acknowledgedBy = userId;
        this.acknowledgedAt = LocalDateTime.now();
        this.status = "ACKNOWLEDGED";
    }
    
    /**
     * Résout l'alerte
     */
    public void resolve() {
        this.resolvedAt = LocalDateTime.now();
        this.status = "RESOLVED";
    }
    
    /**
     * AlertType - Type d'alerte
     */
    public enum AlertType {
        LOW_STOCK,           // Stock bas
        CRITICAL_STOCK,      // Stock critique
        OVERSTOCK,           // Surstock
        EXPIRATION_SOON,     // Péremption imminente
        EXPIRED,             // Produit expiré
        SLOW_ROTATION,       // Rotation lente
        REORDER_NEEDED,      // Réapprovisionnement nécessaire
        BUFFER_INSUFFICIENT  // Tampon insuffisant pour dotations
    }
    
    /**
     * AlertPriority - Niveau de priorité
     */
    public enum AlertPriority {
        CRITICAL,  // Notification immédiate + email + SMS
        HIGH,      // Notification immédiate + email
        MEDIUM,    // Notification dans l'heure
        LOW        // Notification quotidienne
    }
}
