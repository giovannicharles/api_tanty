package com.NTFOODS.Api_tanty.modules.stock.domain.common.entity;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * StockItem - Entité représentant le stock d'un produit dans une localisation
 * Gère la quantité, le conditionnement et les métadonnées
 */
public class StockItem {
    
    private Long id;
    private final StockLocationId locationId;
    private final Long productId;
    private final String productSku;
    private final String packagingType; // SACHET, ETUI, SEAU, CARTON, etc.
    private BigDecimal quantity; // Quantité en unités de base
    private BigDecimal quantityPerCarton; // Quantité par carton/conditionnement
    private BigDecimal reorderPoint;       // Seuil de réapprovisionnement (défini par le DG)
    private BigDecimal safetyStock;        // Stock de sécurité (défini par le DG)
    private BigDecimal unitWeight; // Poids unitaire en grammes
    private String volume; // Volume en L ou mL
    private Integer cartonsPerAssortiment; // Nombre de cartons par assortiment
    private LocalDateTime lastUpdated;
    private UserId lastUpdatedBy;
    
    public StockItem(StockLocationId locationId, Long productId, String productSku, 
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
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public String getPackagingType() {
        return packagingType;
    }
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public BigDecimal getQuantityPerCarton() {
        return quantityPerCarton;
    }

    public BigDecimal getReorderPoint() {
        return reorderPoint;
    }

    public void setReorderPoint(BigDecimal reorderPoint) {
        this.reorderPoint = reorderPoint;
        this.lastUpdated = LocalDateTime.now();
    }

    public BigDecimal getSafetyStock() {
        return safetyStock;
    }

    public void setSafetyStock(BigDecimal safetyStock) {
        this.safetyStock = safetyStock;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void setQuantityPerCarton(BigDecimal quantityPerCarton) {
        this.quantityPerCarton = quantityPerCarton;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public BigDecimal getUnitWeight() {
        return unitWeight;
    }
    
    public void setUnitWeight(BigDecimal unitWeight) {
        this.unitWeight = unitWeight;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getVolume() {
        return volume;
    }
    
    public void setVolume(String volume) {
        this.volume = volume;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public Integer getCartonsPerAssortiment() {
        return cartonsPerAssortiment;
    }
    
    public void setCartonsPerAssortiment(Integer cartonsPerAssortiment) {
        this.cartonsPerAssortiment = cartonsPerAssortiment;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public UserId getLastUpdatedBy() {
        return lastUpdatedBy;
    }
    
    public void setLastUpdatedBy(UserId lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
    
    /**
     * Ajoute une quantité au stock
     */
    public void addQuantity(BigDecimal quantityToAdd, UserId updatedBy) {
        this.quantity = this.quantity.add(quantityToAdd);
        this.lastUpdatedBy = updatedBy;
        this.lastUpdated = LocalDateTime.now();
    }
    
    /**
     * Soustrait une quantité du stock
     */
    public void subtractQuantity(BigDecimal quantityToSubtract, UserId updatedBy) {
        if (quantityToSubtract.compareTo(this.quantity) > 0) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + this.quantity + ", Requested: " + quantityToSubtract);
        }
        this.quantity = this.quantity.subtract(quantityToSubtract);
        this.lastUpdatedBy = updatedBy;
        this.lastUpdated = LocalDateTime.now();
    }
    
    /**
     * Calcule le nombre de cartons
     */
    public BigDecimal calculateCartons() {
        if (quantityPerCarton != null && quantityPerCarton.compareTo(BigDecimal.ZERO) > 0) {
            return quantity.divide(quantityPerCarton, 2, java.math.RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
}
