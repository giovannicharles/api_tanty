package com.NTFOODS.Api_tanty.modules.stock.application.reception.command;

public class RejectReceiptCommand {
    private final String receiptNumber;
    private final String rejectedByMatricule;
    private final String reason;

    public RejectReceiptCommand(String receiptNumber, String rejectedByMatricule, String reason) {
        this.receiptNumber = receiptNumber;
        this.rejectedByMatricule = rejectedByMatricule;
        this.reason = reason;
    }

    public String getReceiptNumber() { return receiptNumber; }
    public String getRejectedByMatricule() { return rejectedByMatricule; }
    public String getReason() { return reason; }
}
