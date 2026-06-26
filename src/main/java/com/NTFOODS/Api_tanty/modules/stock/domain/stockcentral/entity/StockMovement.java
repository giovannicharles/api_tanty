package com.NTFOODS.Api_tanty.modules.stock.domain.stockcentral.entity;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.WarehouseId;
import com.NTFOODS.Api_tanty.modules.stock.domain.stockcentral.enums.MovementStatus;
import com.NTFOODS.Api_tanty.modules.stock.domain.stockcentral.enums.MovementType;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import java.time.LocalDateTime;

/** Mouvement de stock pour traçabilité. */
public class StockMovement {
    private final Long id;
    private final MovementType type;
    private final ProductId productId;
    private final WarehouseId warehouseId;
    private final Quantity quantity;
    private final Quantity previousStock;
    private final Quantity newStock;
    private final String reference;
    private final LocalDateTime createdAt;
    private final UserId createdBy;
    private final MovementStatus status;

    public StockMovement(Long id, MovementType type, ProductId productId, WarehouseId warehouseId, Quantity quantity, Quantity previousStock, Quantity newStock, String reference, LocalDateTime createdAt, UserId createdBy, MovementStatus status) {
        this.id = id;
        this.type = type;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
        this.previousStock = previousStock;
        this.newStock = newStock;
        this.reference = reference;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.status = status;
    }

    // getters
}