package com.NTFOODS.Api_tanty.modules.stock.domain.purchase.entity;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.Money;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;

/** Ligne d'un bon de commande fournisseur. */
public class PurchaseOrderItem {
    private final ProductId productId;
    private final Quantity orderedQty;
    private Quantity receivedQty;   // cumul des réceptions
    private final Money unitPrice;

    public PurchaseOrderItem(ProductId productId, Quantity orderedQty, Money unitPrice) {
        this.productId = productId;
        this.orderedQty = orderedQty;
        this.receivedQty = new Quantity(java.math.BigDecimal.ZERO, orderedQty.getUnit());
        this.unitPrice = unitPrice;
    }

    public void addReceivedQty(Quantity qty) {
        if (qty.getValue().compareTo(orderedQty.getValue().subtract(receivedQty.getValue())) > 0)
            throw new IllegalArgumentException("La réception dépasse la quantité commandée");
        this.receivedQty = this.receivedQty.add(qty);
    }

    public boolean isFullyReceived() {
        return receivedQty.getValue().compareTo(orderedQty.getValue()) >= 0;
    }

    public ProductId getProductId() {
        return productId;
    }
}
