package com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.entity;

import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;

import java.math.BigDecimal;

/** Ligne d'une commande interne. */
public class InternalOrderItem {
    private final ProductId productId;
    private final Quantity requestedQty;
    private Quantity deliveredQty;
    private final String productSku;
    private final String productName;
    private final String productUnit;
    private final String packagingType;
    private final BigDecimal quantityPerCarton;
    private final String notes;

    public InternalOrderItem(ProductId productId, Quantity requestedQty) {
        this(productId, requestedQty, null, null, null, null, null, null);
    }

    public InternalOrderItem(ProductId productId, Quantity requestedQty,
                             String productSku, String productName, String productUnit,
                             String packagingType, BigDecimal quantityPerCarton, String notes) {
        this.productId = productId;
        this.requestedQty = requestedQty;
        this.deliveredQty = new Quantity(BigDecimal.ZERO, requestedQty.getUnit());
        this.productSku = productSku;
        this.productName = productName;
        this.productUnit = productUnit;
        this.packagingType = packagingType;
        this.quantityPerCarton = quantityPerCarton;
        this.notes = notes;
    }

    public void addDeliveredQty(Quantity qty) {
        BigDecimal remaining = requestedQty.getValue().subtract(deliveredQty.getValue());
        if (qty.getValue().compareTo(remaining) > 0)
            throw new IllegalArgumentException("Livraison dépasse la quantité restante pour " + (productName != null ? productName : productId.getValue()));
        this.deliveredQty = this.deliveredQty.add(qty);
    }

    public boolean isFullyDelivered() {
        return deliveredQty.getValue().compareTo(requestedQty.getValue()) >= 0;
    }

    public ProductId getProductId() { return productId; }
    public Quantity getRequestedQty() { return requestedQty; }
    public Quantity getDeliveredQty() { return deliveredQty; }
    public String getProductSku() { return productSku; }
    public String getProductName() { return productName; }
    public String getProductUnit() { return productUnit; }
    public String getPackagingType() { return packagingType; }
    public BigDecimal getQuantityPerCarton() { return quantityPerCarton; }
    public String getNotes() { return notes; }
}
