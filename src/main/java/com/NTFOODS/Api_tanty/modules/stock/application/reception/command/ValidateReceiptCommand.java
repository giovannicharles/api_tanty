package com.NTFOODS.Api_tanty.modules.stock.application.reception.command;

public class ValidateReceiptCommand {
    private final String receiptNumber;
    private final String validatorMatricule;
    private final String notes;
    private final String authCode;

    public ValidateReceiptCommand(String receiptNumber, String validatorMatricule, String notes, String authCode) {
        this.receiptNumber = receiptNumber;
        this.validatorMatricule = validatorMatricule;
        this.notes = notes;
        this.authCode = authCode;
    }

    public String getReceiptNumber() { return receiptNumber; }
    public String getValidatorMatricule() { return validatorMatricule; }
    public String getNotes() { return notes; }
    public String getAuthCode() { return authCode; }
}
