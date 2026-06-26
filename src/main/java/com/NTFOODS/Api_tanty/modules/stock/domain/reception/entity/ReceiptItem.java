package com.NTFOODS.Api_tanty.modules.stock.domain.reception.entity;

import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;

/** Ligne de réception : produit commandé vs reçu. */
public class ReceiptItem {
    private final ProductId productId;
    private final Quantity orderedQty;
    private Quantity receivedQty;
    private String deviationReason;

    public ReceiptItem(ProductId productId, Quantity orderedQty) {
        this.productId = productId;
        this.orderedQty = orderedQty;
        this.receivedQty = new Quantity(java.math.BigDecimal.ZERO, orderedQty.getUnit());
    }

    public void setReceivedQty(Quantity receivedQty, String reason) {
        if (!receivedQty.getUnit().equals(orderedQty.getUnit()))
            throw new IllegalArgumentException("Unité incompatible");
        this.receivedQty = receivedQty;
        if (!isExactMatch()) {
            this.deviationReason = reason;
        }
    }

    public boolean isExactMatch() {
        return orderedQty.getValue().compareTo(receivedQty.getValue()) == 0;
    }

    public ProductId getDifference() {
        return productId;
    }

    public ProductId getProductId() {
        return productId;
    }

    // Getters
}