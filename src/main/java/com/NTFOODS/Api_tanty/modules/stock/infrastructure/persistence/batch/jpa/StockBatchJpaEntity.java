package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.batch.jpa;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * StockBatchJpaEntity - Entité JPA pour les lots de stock.
 */
@Entity
@Table(name = "stock_batches", indexes = {
        @Index(name = "idx_batch_number", columnList = "batch_number", unique = true),
        @Index(name = "idx_batch_product", columnList = "product_sku"),
        @Index(name = "idx_batch_expiry", columnList = "expiry_date")
})
public class StockBatchJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_number", nullable = false, unique = true)
    private String batchNumber;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_sku", nullable = false)
    private String productSku;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "initial_quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal initialQuantity;

    @Column(name = "remaining_quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal remainingQuantity;

    @Column(name = "location_id")
    private String locationId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_movement_at")
    private LocalDateTime lastMovementAt;

    public StockBatchJpaEntity() {}

    public StockBatchJpaEntity(String batchNumber, Long productId, String productSku, String productName,
                               String supplierName, LocalDate manufactureDate, LocalDate expiryDate,
                               BigDecimal initialQuantity, BigDecimal remainingQuantity, String locationId,
                               String status, String notes) {
        this.batchNumber = batchNumber;
        this.productId = productId;
        this.productSku = productSku;
        this.productName = productName;
        this.supplierName = supplierName;
        this.manufactureDate = manufactureDate;
        this.expiryDate = expiryDate;
        this.initialQuantity = initialQuantity;
        this.remainingQuantity = remainingQuantity;
        this.locationId = locationId;
        this.status = status;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public LocalDate getManufactureDate() { return manufactureDate; }
    public void setManufactureDate(LocalDate manufactureDate) { this.manufactureDate = manufactureDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public BigDecimal getInitialQuantity() { return initialQuantity; }
    public void setInitialQuantity(BigDecimal initialQuantity) { this.initialQuantity = initialQuantity; }

    public BigDecimal getRemainingQuantity() { return remainingQuantity; }
    public void setRemainingQuantity(BigDecimal remainingQuantity) { this.remainingQuantity = remainingQuantity; }

    public String getLocationId() { return locationId; }
    public void setLocationId(String locationId) { this.locationId = locationId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastMovementAt() { return lastMovementAt; }
    public void setLastMovementAt(LocalDateTime lastMovementAt) { this.lastMovementAt = lastMovementAt; }
}
