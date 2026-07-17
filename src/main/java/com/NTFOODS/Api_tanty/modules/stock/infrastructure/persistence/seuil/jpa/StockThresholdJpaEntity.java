package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.seuil.jpa;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * StockThresholdJpaEntity - Entité JPA pour StockThreshold
 */
@Entity
@Table(name = "stock_thresholds")
public class StockThresholdJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "location_id", nullable = false)
    private UUID locationId;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(name = "product_sku", nullable = false)
    private String productSku;
    
    @Column(name = "minimum_threshold", nullable = false, precision = 19, scale = 4)
    private BigDecimal minimumThreshold;
    
    @Column(name = "maximum_threshold", nullable = false, precision = 19, scale = 4)
    private BigDecimal maximumThreshold;
    
    @Column(name = "reorder_threshold", nullable = false, precision = 19, scale = 4)
    private BigDecimal reorderThreshold;
    
    @Column(name = "reorder_quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal reorderQuantity;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    public StockThresholdJpaEntity() {}
    
    public StockThresholdJpaEntity(UUID locationId, Long productId, String productSku,
                                 BigDecimal minimumThreshold, BigDecimal maximumThreshold,
                                 BigDecimal reorderThreshold, BigDecimal reorderQuantity, String notes) {
        this.locationId = locationId;
        this.productId = productId;
        this.productSku = productSku;
        this.minimumThreshold = minimumThreshold;
        this.maximumThreshold = maximumThreshold;
        this.reorderThreshold = reorderThreshold;
        this.reorderQuantity = reorderQuantity;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public UUID getLocationId() { return locationId; }
    public void setLocationId(UUID locationId) { this.locationId = locationId; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }
    
    public BigDecimal getMinimumThreshold() { return minimumThreshold; }
    public void setMinimumThreshold(BigDecimal minimumThreshold) { this.minimumThreshold = minimumThreshold; }
    
    public BigDecimal getMaximumThreshold() { return maximumThreshold; }
    public void setMaximumThreshold(BigDecimal maximumThreshold) { this.maximumThreshold = maximumThreshold; }
    
    public BigDecimal getReorderThreshold() { return reorderThreshold; }
    public void setReorderThreshold(BigDecimal reorderThreshold) { this.reorderThreshold = reorderThreshold; }
    
    public BigDecimal getReorderQuantity() { return reorderQuantity; }
    public void setReorderQuantity(BigDecimal reorderQuantity) { this.reorderQuantity = reorderQuantity; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
