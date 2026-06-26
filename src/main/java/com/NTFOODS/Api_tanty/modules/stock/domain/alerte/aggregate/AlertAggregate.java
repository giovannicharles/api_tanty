package com.NTFOODS.Api_tanty.modules.stock.domain.alerte.aggregate;

import com.NTFOODS.Api_tanty.modules.stock.domain.alerte.enums.AlertType;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import java.time.LocalDateTime;

public class AlertAggregate {
    private final Long id;
    private final AlertType type;
    private final String message;
    private final LocalDateTime createdAt;
    private boolean resolved;
    private UserId resolvedBy;

    public AlertAggregate(Long id, AlertType type, String message, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.createdAt = createdAt;
    }

    // constructeur, getters, resolve()
}