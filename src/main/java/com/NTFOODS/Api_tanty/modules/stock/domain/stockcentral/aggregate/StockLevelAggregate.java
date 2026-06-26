package com.NTFOODS.Api_tanty.modules.stock.domain.stockcentral.aggregate;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.WarehouseId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;

/** Stock réel d'un produit dans un entrepôt. */
public class StockLevelAggregate {
    private final ProductId productId;
    private final WarehouseId warehouseId;
    private Quantity quantity;
    private Quantity reservedQty;
    private Quantity reorderPoint;
    private Quantity safetyStock;

    public StockLevelAggregate(ProductId productId, WarehouseId warehouseId, Quantity initialQty) {
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantity = initialQty;
        this.reservedQty = new Quantity(java.math.BigDecimal.ZERO, initialQty.getUnit());
    }

    public void increase(Quantity qty) { this.quantity = this.quantity.add(qty); }
    public void decrease(Quantity qty) {
        if (qty.getValue().compareTo(this.quantity.getValue()) > 0)
            throw new IllegalArgumentException("Stock insuffisant");
        this.quantity = this.quantity.subtract(qty);
    }
    public void reserve(Quantity qty) {
        if (qty.getValue().compareTo(this.quantity.subtract(this.reservedQty).getValue()) > 0)
            throw new IllegalArgumentException("Stock disponible insuffisant");
        this.reservedQty = this.reservedQty.add(qty);
    }
    public void releaseReservation(Quantity qty) { this.reservedQty = this.reservedQty.subtract(qty); }

    // Getters
}