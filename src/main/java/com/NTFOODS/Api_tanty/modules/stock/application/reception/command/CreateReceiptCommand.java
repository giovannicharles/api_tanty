package com.NTFOODS.Api_tanty.modules.stock.application.reception.command;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.WarehouseId;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.SourceType;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.entity.ReceiptItem;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;

import java.time.LocalDate;
import java.util.List;

/**
 * CreateReceiptCommand - Commande pour créer une réception
 * Utilisée pour les réceptions fournisseurs et les réceptions de production
 */
public record CreateReceiptCommand(
        SourceType source,
        Long sourceId,
        LocalDate receiptDate,
        WarehouseId warehouseId,
        List<ReceiptItemDTO> items
) {
    public record ReceiptItemDTO(
            ProductId productId,
            Quantity orderedQty
    ) {}
}
