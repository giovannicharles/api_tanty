package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.alerte.jpa;

import com.NTFOODS.Api_tanty.modules.stock.domain.alerte.entity.StockAlert;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * StockAlertJpaEntity - Entité JPA pour StockAlert
 */
@Entity
@Table(name = "stock_alerts")
public class StockAlertJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private StockAlert.AlertType type;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private StockAlert.AlertPriority priority;
    
    @Column(name = "location_id", nullable = false)
    private UUID locationId;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(name = "product_sku", nullable = false)
    private String productSku;
    
    @Column(name = "product_name", nullable = false)
    private String productName;
    
    @Column(name = "current_quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal currentQuantity;
    
    @Column(name = "threshold", nullable = false, precision = 19, scale = 4)
    private BigDecimal threshold;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "acknowledged", nullable = false)
    private boolean acknowledged;
    
    @Column(name = "acknowledged_by")
    private String acknowledgedBy;
    
    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    public StockAlertJpaEntity() {}
    
    public StockAlertJpaEntity(StockAlert.AlertType type, StockAlert.AlertPriority priority, UUID locationId,
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
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public StockAlert.AlertType getType() { return type; }
    public void setType(StockAlert.AlertType type) { this.type = type; }
    
    public StockAlert.AlertPriority getPriority() { return priority; }
    public void setPriority(StockAlert.AlertPriority priority) { this.priority = priority; }
    
    public UUID getLocationId() { return locationId; }
    public void setLocationId(UUID locationId) { this.locationId = locationId; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public BigDecimal getCurrentQuantity() { return currentQuantity; }
    public void setCurrentQuantity(BigDecimal currentQuantity) { this.currentQuantity = currentQuantity; }
    
    public BigDecimal getThreshold() { return threshold; }
    public void setThreshold(BigDecimal threshold) { this.threshold = threshold; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public boolean isAcknowledged() { return acknowledged; }
    public void setAcknowledged(boolean acknowledged) { this.acknowledged = acknowledged; }
    
    public String getAcknowledgedBy() { return acknowledgedBy; }
    public void setAcknowledgedBy(String acknowledgedBy) { this.acknowledgedBy = acknowledgedBy; }
    
    public LocalDateTime getAcknowledgedAt() { return acknowledgedAt; }
    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) { this.acknowledgedAt = acknowledgedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
