package com.NTFOODS.Api_tanty.modules.stock.domain.reception.events;

import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceptionType;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject.ReceiptNumber;
import com.NTFOODS.Api_tanty.shared.kernel.event.DomainEvent;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import java.time.LocalDateTime;

public class ReceiptCreatedEvent implements DomainEvent {
    private final ReceiptNumber receiptNumber;
    private final ReceptionType type;
    private final UserId createdBy;
    private final LocalDateTime occurredOn;

    public ReceiptCreatedEvent(ReceiptNumber receiptNumber, ReceptionType type, UserId createdBy) {
        this.receiptNumber = receiptNumber;
        this.type = type;
        this.createdBy = createdBy;
        this.occurredOn = LocalDateTime.now();
    }

    public ReceiptNumber getReceiptNumber() { return receiptNumber; }
    public ReceptionType getType() { return type; }
    public UserId getCreatedBy() { return createdBy; }
    public LocalDateTime getOccurredOn() { return occurredOn; }
}
