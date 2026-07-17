package com.NTFOODS.Api_tanty.modules.stock.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * InternalOrderResponse - DTO de réponse pour les commandes internes Stock → Production.
 * Remplace l'exposition directe de InternalOrderJpaEntity.
 */
public class InternalOrderResponse {
    private Long id;
    private String orderNumber;
    private LocalDate orderDate;
    private String status;
    private String requestedBy;
    private String requestedByName;
    private String approvedBy;
    private String approvedByName;
    private LocalDateTime approvedAt;
    private String cancelledBy;
    private String cancelledReason;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;
    private String notes;
    private List<ItemResponse> items;

    public static class ItemResponse {
        private Long id;
        private Long productId;
        private String productSku;
        private String productName;
        private String productUnit;
        private String packagingType;
        private BigDecimal quantityPerCarton;
        private BigDecimal requestedQty;
        private BigDecimal deliveredQty;
        private boolean fullyDelivered;
        private String notes;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductSku() { return productSku; }
        public void setProductSku(String productSku) { this.productSku = productSku; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getProductUnit() { return productUnit; }
        public void setProductUnit(String productUnit) { this.productUnit = productUnit; }
        public String getPackagingType() { return packagingType; }
        public void setPackagingType(String packagingType) { this.packagingType = packagingType; }
        public BigDecimal getQuantityPerCarton() { return quantityPerCarton; }
        public void setQuantityPerCarton(BigDecimal quantityPerCarton) { this.quantityPerCarton = quantityPerCarton; }
        public BigDecimal getRequestedQty() { return requestedQty; }
        public void setRequestedQty(BigDecimal requestedQty) { this.requestedQty = requestedQty; }
        public BigDecimal getDeliveredQty() { return deliveredQty; }
        public void setDeliveredQty(BigDecimal deliveredQty) { this.deliveredQty = deliveredQty; }
        public boolean isFullyDelivered() { return fullyDelivered; }
        public void setFullyDelivered(boolean fullyDelivered) { this.fullyDelivered = fullyDelivered; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }
    public String getRequestedByName() { return requestedByName; }
    public void setRequestedByName(String requestedByName) { this.requestedByName = requestedByName; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public String getCancelledBy() { return cancelledBy; }
    public void setCancelledBy(String cancelledBy) { this.cancelledBy = cancelledBy; }
    public String getCancelledReason() { return cancelledReason; }
    public void setCancelledReason(String cancelledReason) { this.cancelledReason = cancelledReason; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<ItemResponse> getItems() { return items; }
    public void setItems(List<ItemResponse> items) { this.items = items; }
}
