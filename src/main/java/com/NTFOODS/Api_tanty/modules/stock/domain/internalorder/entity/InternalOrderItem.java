package com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.entity;

import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;

/** Ligne d'une commande interne. */
public class InternalOrderItem {
    private final ProductId productId;
    private final Quantity requestedQty;
    private Quantity deliveredQty;

    public InternalOrderItem(ProductId productId, Quantity requestedQty) {
        this.productId = productId;
        this.requestedQty = requestedQty;
        this.deliveredQty = new Quantity(java.math.BigDecimal.ZERO, requestedQty.getUnit());
    }

    public void addDeliveredQty(Quantity qty) {
        if (qty.getValue().compareTo(requestedQty.getValue().subtract(deliveredQty.getValue())) > 0)
            throw new IllegalArgumentException("Livraison dépasse commande");
        this.deliveredQty = this.deliveredQty.add(qty);
    }

    public boolean isFullyDelivered() {
        return deliveredQty.getValue().compareTo(requestedQty.getValue()) >= 0;
    }

    public ProductId getProductId() {
        return productId;
    }
    // getters
}
