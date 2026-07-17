package com.NTFOODS.Api_tanty.modules.stock.application.dto;

/**
 * CancelOrderRequest - DTO pour annuler une commande interne.
 */
public class CancelOrderRequest {
    private String cancelledBy;
    private String reason;

    public String getCancelledBy() { return cancelledBy; }
    public void setCancelledBy(String cancelledBy) { this.cancelledBy = cancelledBy; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
