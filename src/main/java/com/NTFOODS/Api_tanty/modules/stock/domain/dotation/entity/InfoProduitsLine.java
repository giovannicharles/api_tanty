package com.NTFOODS.Api_tanty.modules.stock.domain.dotation.entity;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.Money;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;

/** Ligne d'une fiche InfoProduits. */
public class InfoProduitsLine {
    private final ProductId productId;
    private Quantity takenQty;      // sortie du stock central
    private Quantity soldQty;       // ventes réelles (calculées ou saisies)
    private Quantity returnedQty;   // invendus (restent chez le commercial)
    private final Money unitPrice;

    public InfoProduitsLine(ProductId productId, Quantity takenQty, Money unitPrice) {
        this.productId = productId;
        this.takenQty = takenQty;
        this.unitPrice = unitPrice;
        this.soldQty = new Quantity(java.math.BigDecimal.ZERO, takenQty.getUnit());
        this.returnedQty = new Quantity(java.math.BigDecimal.ZERO, takenQty.getUnit());
    }

    public void updateSoldQty(Quantity sold) {
        if (sold.getValue().compareTo(takenQty.getValue()) > 0)
            throw new IllegalArgumentException("Ventes > quantité prise");
        this.soldQty = sold;
        this.returnedQty = takenQty.subtract(sold);
    }
}