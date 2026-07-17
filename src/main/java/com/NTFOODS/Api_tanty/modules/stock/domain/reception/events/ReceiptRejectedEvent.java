package com.NTFOODS.Api_tanty.modules.stock.domain.reception.events;

import com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject.ReceiptNumber;
import com.NTFOODS.Api_tanty.shared.kernel.event.DomainEvent;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import java.time.LocalDateTime;

public class ReceiptRejectedEvent implements DomainEvent {
    private final ReceiptNumber receiptNumber;
    private final UserId rejectedBy;
    private final String reason;
    private final LocalDateTime occurredOn;

    public ReceiptRejectedEvent(ReceiptNumber receiptNumber, UserId rejectedBy, String reason) {
        this.receiptNumber = receiptNumber;
        this.rejectedBy = rejectedBy;
        this.reason = reason;
        this.occurredOn = LocalDateTime.now();
    }

    public ReceiptNumber getReceiptNumber() { return receiptNumber; }
    public UserId getRejectedBy() { return rejectedBy; }
    public String getReason() { return reason; }
    public LocalDateTime getOccurredOn() { return occurredOn; }
}
