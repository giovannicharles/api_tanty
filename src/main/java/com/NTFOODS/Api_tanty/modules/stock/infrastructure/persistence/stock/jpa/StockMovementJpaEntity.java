package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.jpa;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockMovementType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * StockMovementJpaEntity - Entité JPA pour StockMovement
 */
@Entity
@Table(name = "stock_movements")
public class StockMovementJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private StockMovementType type;
    
    @Column(name = "from_location_id")
    private UUID fromLocationId;
    
    @Column(name = "to_location_id")
    private UUID toLocationId;
    
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
    
    @Column(name = "requested_by")
    private UUID requestedBy;
    
    @Column(name = "validated_by")
    private UUID validatedBy;
    
    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;
    
    @Column(name = "validated_at")
    private LocalDateTime validatedAt;
    
    @Column(name = "reference_number")
    private String referenceNumber;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    public StockMovementJpaEntity() {}
    
    public StockMovementJpaEntity(StockMovementType type, UUID fromLocationId, UUID toLocationId,
                                 Long productId, String productSku, String packagingType,
                                 BigDecimal quantity, BigDecimal quantityPerCarton,
                                 UUID requestedBy, String referenceNumber, String notes) {
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
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public StockMovementType getType() { return type; }
    public void setType(StockMovementType type) { this.type = type; }
    
    public UUID getFromLocationId() { return fromLocationId; }
    public void setFromLocationId(UUID fromLocationId) { this.fromLocationId = fromLocationId; }
    
    public UUID getToLocationId() { return toLocationId; }
    public void setToLocationId(UUID toLocationId) { this.toLocationId = toLocationId; }
    
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
    
    public UUID getRequestedBy() { return requestedBy; }
    public void setRequestedBy(UUID requestedBy) { this.requestedBy = requestedBy; }
    
    public UUID getValidatedBy() { return validatedBy; }
    public void setValidatedBy(UUID validatedBy) { this.validatedBy = validatedBy; }
    
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    
    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
