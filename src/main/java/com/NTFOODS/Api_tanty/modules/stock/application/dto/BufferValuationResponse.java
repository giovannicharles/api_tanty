package com.NTFOODS.Api_tanty.modules.stock.application.dto;

import java.math.BigDecimal;
import java.util.List;

public class BufferValuationResponse {
    private BigDecimal totalValue;
    private int itemCount;
    private String currency;
    private List<BufferValuationItem> items;

    public BufferValuationResponse() {}

    public BufferValuationResponse(BigDecimal totalValue, int itemCount, String currency, List<BufferValuationItem> items) {
        this.totalValue = totalValue;
        this.itemCount = itemCount;
        this.currency = currency;
        this.items = items;
    }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public List<BufferValuationItem> getItems() { return items; }
    public void setItems(List<BufferValuationItem> items) { this.items = items; }

    public static class BufferValuationItem {
        private String productSku;
        private String productName;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalValue;
        private String priceType;
        private String currency;

        public BufferValuationItem() {}

        public BufferValuationItem(String productSku, String productName, BigDecimal quantity,
                                   BigDecimal unitPrice, BigDecimal totalValue, String priceType, String currency) {
            this.productSku = productSku;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalValue = totalValue;
            this.priceType = priceType;
            this.currency = currency;
        }

        public String getProductSku() { return productSku; }
        public void setProductSku(String productSku) { this.productSku = productSku; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        public BigDecimal getTotalValue() { return totalValue; }
        public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
        public String getPriceType() { return priceType; }
        public void setPriceType(String priceType) { this.priceType = priceType; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }
}
