package com.NTFOODS.Api_tanty.modules.stock.application.reception.command;

import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceiptStatus;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject.ReceiptNumber;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

/**
 * ValidateReceiptCommand - Commande pour valider une réception
 * Utilisée pour la première et la seconde validation
 */
public record ValidateReceiptCommand(
        ReceiptNumber receiptNumber,
        ReceiptStatus currentStatus,
        UserId validator,
        String notes,
        String authCode
) {}
