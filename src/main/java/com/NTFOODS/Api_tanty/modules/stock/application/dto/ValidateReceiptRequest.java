package com.NTFOODS.Api_tanty.modules.stock.application.dto;

public class ValidateReceiptRequest {
    private String notes;
    private String authCode;

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getAuthCode() { return authCode; }
    public void setAuthCode(String authCode) { this.authCode = authCode; }
}
