package com.NTFOODS.Api_tanty.modules.stock.domain.productionbatch.aggregate;

import com.NTFOODS.Api_tanty.modules.stock.domain.productionbatch.enums.ProductionStatus;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import java.time.LocalDate;

/** Lot de production déclaré par la production, puis validé par le stock. */
public class ProductionBatchAggregate {
    private final Long id;
    private final ProductId productId;
    private final Quantity declaredQuantity;
    private final LocalDate productionDate;
    private final UserId declaredBy;
    private ProductionStatus status;
    private UserId stockValidator;

    public ProductionBatchAggregate(Long id, ProductId productId, Quantity declaredQuantity,
                                    LocalDate productionDate, UserId declaredBy) {

        this.id = id;
        this.productId = productId;
        this.declaredQuantity = declaredQuantity;
        this.productionDate = productionDate;
        this.declaredBy = declaredBy;
        this.status = ProductionStatus.DECLARED_BY_PRODUCTION;
    }

    public void validateByStock(UserId validator) {
        if (status != ProductionStatus.DECLARED_BY_PRODUCTION)
            throw new IllegalStateException("Seul un lot déclaré peut être validé");
        this.stockValidator = validator;
        this.status = ProductionStatus.VALIDATED_BY_STOCK;
    }

    // Getters
}