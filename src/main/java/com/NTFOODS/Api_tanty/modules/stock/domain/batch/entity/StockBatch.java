package com.NTFOODS.Api_tanty.modules.stock.domain.batch.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * StockBatch - Lot de stock pour traçabilité FIFO/FEFO.
 */
public class StockBatch {

    private Long id;
    private String batchNumber;
    private Long productId;
    private String productSku;
    private String productName;
    private String supplierName;
    private LocalDate manufactureDate;
    private LocalDate expiryDate;
    private BigDecimal initialQuantity;
    private BigDecimal remainingQuantity;
    private String locationId;
    private String status; // AVAILABLE, RESERVED, EXPIRED, EMPTY, QUARANTINE
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime lastMovementAt;

    public StockBatch(String batchNumber, Long productId, String productSku, String productName,
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

    public boolean isExpired() {
        return expiryDate != null && !LocalDate.now().isBefore(expiryDate);
    }

    public boolean isExpiringSoon(int days) {
        if (expiryDate == null) return false;
        LocalDate threshold = LocalDate.now().plusDays(days);
        return !expiryDate.isAfter(threshold) && !isExpired();
    }

    public void consume(BigDecimal quantity) {
        if (this.remainingQuantity == null) {
            throw new IllegalStateException("Remaining quantity not initialized");
        }
        if (quantity.compareTo(this.remainingQuantity) > 0) {
            throw new IllegalArgumentException("Insufficient quantity in batch");
        }
        this.remainingQuantity = this.remainingQuantity.subtract(quantity);
        if (this.remainingQuantity.compareTo(BigDecimal.ZERO) == 0) {
            this.status = "EMPTY";
        }
        this.lastMovementAt = LocalDateTime.now();
    }
}
