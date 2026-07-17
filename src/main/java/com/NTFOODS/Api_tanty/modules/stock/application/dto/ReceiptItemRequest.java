package com.NTFOODS.Api_tanty.modules.stock.application.dto;

import java.math.BigDecimal;

public class ReceiptItemRequest {
    private Long productId;
    private String productName;
    private String productSku;
    private String productUnit;
    private String packagingType;
    private BigDecimal quantityPerCarton;
    private BigDecimal orderedQty;
    private BigDecimal receivedQty;
    private String deviationReason;
    private String lotNumber;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }
    public String getProductUnit() { return productUnit; }
    public void setProductUnit(String productUnit) { this.productUnit = productUnit; }
    public String getPackagingType() { return packagingType; }
    public void setPackagingType(String packagingType) { this.packagingType = packagingType; }
    public BigDecimal getQuantityPerCarton() { return quantityPerCarton; }
    public void setQuantityPerCarton(BigDecimal quantityPerCarton) { this.quantityPerCarton = quantityPerCarton; }
    public BigDecimal getOrderedQty() { return orderedQty; }
    public void setOrderedQty(BigDecimal orderedQty) { this.orderedQty = orderedQty; }
    public BigDecimal getReceivedQty() { return receivedQty; }
    public void setReceivedQty(BigDecimal receivedQty) { this.receivedQty = receivedQty; }
    public String getDeviationReason() { return deviationReason; }
    public void setDeviationReason(String deviationReason) { this.deviationReason = deviationReason; }
    public String getLotNumber() { return lotNumber; }
    public void setLotNumber(String lotNumber) { this.lotNumber = lotNumber; }
}
