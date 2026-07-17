package com.NTFOODS.Api_tanty.modules.stock.application.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * CreateInternalOrderRequest - DTO pour créer une commande interne Stock → Production.
 */
public class CreateInternalOrderRequest {
    private String requestedBy;
    private String requestedByName;
    private String notes;
    private List<ItemRequest> items;

    public static class ItemRequest {
        private Long productId;
        private String productSku;
        private String productName;
        private String productUnit;
        private String packagingType;
        private BigDecimal quantityPerCarton;
        private BigDecimal requestedQty;
        private String notes;

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
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }
    public String getRequestedByName() { return requestedByName; }
    public void setRequestedByName(String requestedByName) { this.requestedByName = requestedByName; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<ItemRequest> getItems() { return items; }
    public void setItems(List<ItemRequest> items) { this.items = items; }
}
