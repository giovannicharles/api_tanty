package com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums;

public enum ReceiptStatus {
    PENDING_FIRST_VALIDATION,   // en attente de la validation du gestionnaire
    PENDING_SECOND_VALIDATION,  // en attente de la validation du chef prod / contrôle général
    VALIDATED,                  // définitivement validée (stock augmenté)
    REJECTED                    // rejetée
}