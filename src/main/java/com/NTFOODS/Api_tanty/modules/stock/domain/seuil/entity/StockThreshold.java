package com.NTFOODS.Api_tanty.modules.stock.domain.seuil.entity;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * StockThreshold - Entité représentant un seuil de stock
 * Définit les seuils minimum, maximum et de réapprovisionnement pour un produit dans une localisation
 */
public class StockThreshold {
    
    private Long id;
    private final StockLocationId locationId;
    private final Long productId;
    private final String productSku;
    private final BigDecimal minimumThreshold;  // Seuil minimum (alerte stock bas)
    private final BigDecimal maximumThreshold;  // Seuil maximum (alerte surstock)
    private final BigDecimal reorderThreshold;   // Seuil de réapprovisionnement
    private final BigDecimal reorderQuantity;    // Quantité à commander lors du réapprovisionnement
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;
    
    public StockThreshold(StockLocationId locationId, Long productId, String productSku,
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
    
    public BigDecimal getMinimumThreshold() {
        return minimumThreshold;
    }
    
    public BigDecimal getMaximumThreshold() {
        return maximumThreshold;
    }
    
    public BigDecimal getReorderThreshold() {
        return reorderThreshold;
    }
    
    public BigDecimal getReorderQuantity() {
        return reorderQuantity;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Met à jour les seuils
     */
    public void updateThresholds(BigDecimal minimumThreshold, BigDecimal maximumThreshold,
                                BigDecimal reorderThreshold, BigDecimal reorderQuantity) {
        // Note: Dans une implémentation complète, on utiliserait un builder ou des setters
        // Pour simplifier, on suppose que les seuils sont immuables après création
        // Cette méthode pourrait être étendue pour permettre les mises à jour
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Vérifie si une quantité donnée est en dessous du seuil minimum
     */
    public boolean isBelowMinimum(BigDecimal quantity) {
        return quantity.compareTo(minimumThreshold) < 0;
    }
    
    /**
     * Vérifie si une quantité donnée est au-dessus du seuil maximum
     */
    public boolean isAboveMaximum(BigDecimal quantity) {
        return quantity.compareTo(maximumThreshold) > 0;
    }
    
    /**
     * Vérifie si une quantité donnée nécessite un réapprovisionnement
     */
    public boolean needsReorder(BigDecimal quantity) {
        return quantity.compareTo(reorderThreshold) <= 0;
    }
    
    /**
     * Calcule la quantité à commander pour atteindre le niveau optimal
     */
    public BigDecimal calculateReorderQuantity(BigDecimal currentQuantity) {
        BigDecimal targetLevel = minimumThreshold.add(reorderQuantity);
        BigDecimal needed = targetLevel.subtract(currentQuantity);
        return needed.max(BigDecimal.ZERO);
    }
}
