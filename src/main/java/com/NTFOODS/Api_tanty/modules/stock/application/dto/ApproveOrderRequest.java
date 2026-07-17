package com.NTFOODS.Api_tanty.modules.stock.application.dto;

/**
 * ApproveOrderRequest - DTO pour approuver une commande interne.
 */
public class ApproveOrderRequest {
    private String approverId;
    private String approverName;

    public String getApproverId() { return approverId; }
    public void setApproverId(String approverId) { this.approverId = approverId; }
    public String getApproverName() { return approverName; }
    public void setApproverName(String approverName) { this.approverName = approverName; }
}
