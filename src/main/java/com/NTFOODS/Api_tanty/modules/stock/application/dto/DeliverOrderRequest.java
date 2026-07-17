package com.NTFOODS.Api_tanty.modules.stock.application.dto;

import java.math.BigDecimal;

/**
 * DeliverOrderRequest - DTO pour enregistrer une livraison sur une commande interne.
 */
public class DeliverOrderRequest {
    private Long productId;
    private BigDecimal deliveredQty;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public BigDecimal getDeliveredQty() { return deliveredQty; }
    public void setDeliveredQty(BigDecimal deliveredQty) { this.deliveredQty = deliveredQty; }
}
