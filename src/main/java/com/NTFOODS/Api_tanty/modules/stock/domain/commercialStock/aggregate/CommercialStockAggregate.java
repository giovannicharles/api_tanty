package com.NTFOODS.Api_tanty.modules.stock.domain.commercialStock.aggregate;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.CommercialId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;

/** Stock mobile d'un commercial (invendus restent ici). */
public class CommercialStockAggregate {
    private final CommercialId commercialId;
    private final ProductId productId;
    private Quantity quantity;

    public CommercialStockAggregate(CommercialId commercialId, ProductId productId, Quantity initialQty) {
        this.commercialId = commercialId;
        this.productId = productId;
        this.quantity = initialQty;
    }

    public void increase(Quantity qty) { this.quantity = this.quantity.add(qty); }
    public void decrease(Quantity qty) { this.quantity = this.quantity.subtract(qty); }

    // Getters
}