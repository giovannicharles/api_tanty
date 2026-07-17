package com.NTFOODS.Api_tanty.modules.stock.domain.common.entity;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockMovementType;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * StockMovement - Entité représentant un mouvement de stock
 * Enregistre tous les flux entrants et sortants
 */
public class StockMovement {
    
    private Long id;
    private final StockMovementType type;
    private final StockLocationId fromLocationId;
    private final StockLocationId toLocationId;
    private final Long productId;
    private final String productSku;
    private final String packagingType;
    private final BigDecimal quantity;
    private final BigDecimal quantityPerCarton;
    private UserId requestedBy;
    private UserId validatedBy;
    private LocalDateTime requestedAt;
    private LocalDateTime validatedAt;
    private String referenceNumber; // Numéro de bon, commande, etc.
    private String notes;
    private String status; // PENDING, VALIDATED, CANCELLED
    
    public StockMovement(StockMovementType type, StockLocationId fromLocationId, 
                         StockLocationId toLocationId, Long productId, String productSku,
                         String packagingType, BigDecimal quantity, BigDecimal quantityPerCarton,
                         UserId requestedBy, String referenceNumber, String notes) {
        this.type = type;
        this.fromLocationId = fromLocationId;
        this.toLocationId = toLocationId;
        this.productId = productId;
        this.productSku = productSku;
        this.packagingType = packagingType;
        this.quantity = quantity;
        this.quantityPerCarton = quantityPerCarton;
        this.requestedBy = requestedBy;
        this.referenceNumber = referenceNumber;
        this.notes = notes;
        this.requestedAt = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public StockMovementType getType() {
        return type;
    }
    
    public StockLocationId getFromLocationId() {
        return fromLocationId;
    }
    
    public StockLocationId getToLocationId() {
        return toLocationId;
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
    
    public BigDecimal getQuantityPerCarton() {
        return quantityPerCarton;
    }
    
    public UserId getRequestedBy() {
        return requestedBy;
    }
    
    public UserId getValidatedBy() {
        return validatedBy;
    }
    
    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Valide le mouvement de stock
     */
    public void validate(UserId validatedBy) {
        this.validatedBy = validatedBy;
        this.validatedAt = LocalDateTime.now();
        this.status = "VALIDATED";
    }

    /**
     * Marque le mouvement comme validé sans écraser l'horodatage d'origine
     */
    public void markValidated(UserId validatedBy, LocalDateTime validatedAt) {
        this.validatedBy = validatedBy;
        this.validatedAt = validatedAt;
        this.status = "VALIDATED";
    }
    
    /**
     * Annule le mouvement de stock
     */
    public void cancel(String reason) {
        this.status = "CANCELLED";
        this.notes = (this.notes != null ? this.notes + "\n" : "") + "CANCELLED: " + reason;
    }
}
