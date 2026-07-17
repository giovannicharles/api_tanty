package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.jpa;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * StockItemJpaEntity - Entité JPA pour StockItem
 */
@Entity
@Table(name = "stock_items")
public class StockItemJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "location_id", nullable = false)
    private UUID locationId;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(name = "product_sku", nullable = false)
    private String productSku;
    
    @Column(name = "packaging_type", nullable = false)
    private String packagingType;
    
    @Column(name = "quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;
    
    @Column(name = "quantity_per_carton", precision = 19, scale = 4)
    private BigDecimal quantityPerCarton;
    
    @Column(name = "unit_weight", precision = 19, scale = 4)
    private BigDecimal unitWeight;
    
    @Column(name = "volume")
    private String volume;
    
    @Column(name = "cartons_per_assortiment")
    private Integer cartonsPerAssortiment;

    @Column(name = "reorder_point", precision = 19, scale = 4)
    private BigDecimal reorderPoint;

    @Column(name = "safety_stock", precision = 19, scale = 4)
    private BigDecimal safetyStock;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
    
    @Column(name = "last_updated_by")
    private UUID lastUpdatedBy;
    
    public StockItemJpaEntity() {}
    
    public StockItemJpaEntity(UUID locationId, Long productId, String productSku,
                            String packagingType, BigDecimal quantity, BigDecimal quantityPerCarton,
                            BigDecimal unitWeight, String volume, Integer cartonsPerAssortiment) {
        this.locationId = locationId;
        this.productId = productId;
        this.productSku = productSku;
        this.packagingType = packagingType;
        this.quantity = quantity;
        this.quantityPerCarton = quantityPerCarton;
        this.unitWeight = unitWeight;
        this.volume = volume;
        this.cartonsPerAssortiment = cartonsPerAssortiment;
        this.lastUpdated = LocalDateTime.now();
    }

    public BigDecimal getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(BigDecimal reorderPoint) { this.reorderPoint = reorderPoint; }

    public BigDecimal getSafetyStock() { return safetyStock; }
    public void setSafetyStock(BigDecimal safetyStock) { this.safetyStock = safetyStock; }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public UUID getLocationId() { return locationId; }
    public void setLocationId(UUID locationId) { this.locationId = locationId; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }
    
    public String getPackagingType() { return packagingType; }
    public void setPackagingType(String packagingType) { this.packagingType = packagingType; }
    
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    
    public BigDecimal getQuantityPerCarton() { return quantityPerCarton; }
    public void setQuantityPerCarton(BigDecimal quantityPerCarton) { this.quantityPerCarton = quantityPerCarton; }
    
    public BigDecimal getUnitWeight() { return unitWeight; }
    public void setUnitWeight(BigDecimal unitWeight) { this.unitWeight = unitWeight; }
    
    public String getVolume() { return volume; }
    public void setVolume(String volume) { this.volume = volume; }
    
    public Integer getCartonsPerAssortiment() { return cartonsPerAssortiment; }
    public void setCartonsPerAssortiment(Integer cartonsPerAssortiment) { this.cartonsPerAssortiment = cartonsPerAssortiment; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    
    public UUID getLastUpdatedBy() { return lastUpdatedBy; }
    public void setLastUpdatedBy(UUID lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }
}
