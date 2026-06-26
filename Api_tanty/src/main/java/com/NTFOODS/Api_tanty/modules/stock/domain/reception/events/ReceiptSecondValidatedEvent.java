package com.NTFOODS.Api_tanty.modules.stock.domain.reception.events;

import com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject.ReceiptNumber;
import com.NTFOODS.Api_tanty.shared.kernel.event.DomainEvent;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import java.time.LocalDateTime;

public class ReceiptSecondValidatedEvent implements DomainEvent {
    private final ReceiptNumber receiptNumber;
    private final UserId validator;
    private final LocalDateTime occurredOn;

    public ReceiptSecondValidatedEvent(ReceiptNumber receiptNumber, UserId validator) {
        this.receiptNumber = receiptNumber;
        this.validator = validator;
        this.occurredOn = LocalDateTime.now();
    }

    // Getters
}