package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.dotation.jpa;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * DotationItemJpaEntity - Entité JPA pour DotationItem
 */
@Entity
@Table(name = "dotation_items")
public class DotationItemJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(name = "product_sku", nullable = false)
    private String productSku;
    
    @Column(name = "product_name", nullable = false)
    private String productName;
    
    @Column(name = "packaging_type", nullable = false)
    private String packagingType;
    
    @Column(name = "requested_quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal requestedQuantity;
    
    @Column(name = "approved_quantity", precision = 19, scale = 4)
    private BigDecimal approvedQuantity;
    
    @Column(name = "quantity_per_carton", precision = 19, scale = 4)
    private BigDecimal quantityPerCarton;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dotation_request_id")
    private DotationRequestJpaEntity dotationRequest;
    
    public DotationItemJpaEntity() {}
    
    public DotationItemJpaEntity(Long productId, String productSku, String productName,
                                String packagingType, BigDecimal requestedQuantity,
                                BigDecimal quantityPerCarton) {
        this.productId = productId;
        this.productSku = productSku;
        this.productName = productName;
        this.packagingType = packagingType;
        this.requestedQuantity = requestedQuantity;
        this.quantityPerCarton = quantityPerCarton;
        this.approvedQuantity = requestedQuantity;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getPackagingType() { return packagingType; }
    public void setPackagingType(String packagingType) { this.packagingType = packagingType; }
    
    public BigDecimal getRequestedQuantity() { return requestedQuantity; }
    public void setRequestedQuantity(BigDecimal requestedQuantity) { this.requestedQuantity = requestedQuantity; }
    
    public BigDecimal getApprovedQuantity() { return approvedQuantity; }
    public void setApprovedQuantity(BigDecimal approvedQuantity) { this.approvedQuantity = approvedQuantity; }
    
    public BigDecimal getQuantityPerCarton() { return quantityPerCarton; }
    public void setQuantityPerCarton(BigDecimal quantityPerCarton) { this.quantityPerCarton = quantityPerCarton; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public DotationRequestJpaEntity getDotationRequest() { return dotationRequest; }
    public void setDotationRequest(DotationRequestJpaEntity dotationRequest) { this.dotationRequest = dotationRequest; }
}
