package com.NTFOODS.Api_tanty.modules.stock.application.dto;

import java.util.ArrayList;
import java.util.List;

public class ReceiptResponse {
    private Long id;
    private String receiptNumber;
    private String receptionType;
    private String receptionTypeLabel;
    private String sourceLabel;
    private Long sourceId;
    private String receiptDate;
    private String destinationLocationId;
    private String destinationLocationName;
    private String status;
    private String statusLabel;
    private String createdBy;
    private String requiredFirstValidatorRole;
    private String requiredSecondValidatorRole;
    private String firstValidator;
    private String firstValidatedAt;
    private String firstValidationNotes;
    private String secondValidator;
    private String secondValidatedAt;
    private String secondValidationNotes;
    private boolean requiresAuthCode;
    private String rejectionReason;
    private String rejectedBy;
    private String rejectedAt;
    private List<ReceiptItemResponse> items = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
    public String getReceptionType() { return receptionType; }
    public void setReceptionType(String receptionType) { this.receptionType = receptionType; }
    public String getReceptionTypeLabel() { return receptionTypeLabel; }
    public void setReceptionTypeLabel(String receptionTypeLabel) { this.receptionTypeLabel = receptionTypeLabel; }
    public String getSourceLabel() { return sourceLabel; }
    public void setSourceLabel(String sourceLabel) { this.sourceLabel = sourceLabel; }
    public Long getSourceId() { return sourceId; }
    public void setSourceId(Long sourceId) { this.sourceId = sourceId; }
    public String getReceiptDate() { return receiptDate; }
    public void setReceiptDate(String receiptDate) { this.receiptDate = receiptDate; }
    public String getDestinationLocationId() { return destinationLocationId; }
    public void setDestinationLocationId(String destinationLocationId) { this.destinationLocationId = destinationLocationId; }
    public String getDestinationLocationName() { return destinationLocationName; }
    public void setDestinationLocationName(String destinationLocationName) { this.destinationLocationName = destinationLocationName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStatusLabel() { return statusLabel; }
    public void setStatusLabel(String statusLabel) { this.statusLabel = statusLabel; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getRequiredFirstValidatorRole() { return requiredFirstValidatorRole; }
    public void setRequiredFirstValidatorRole(String requiredFirstValidatorRole) { this.requiredFirstValidatorRole = requiredFirstValidatorRole; }
    public String getRequiredSecondValidatorRole() { return requiredSecondValidatorRole; }
    public void setRequiredSecondValidatorRole(String requiredSecondValidatorRole) { this.requiredSecondValidatorRole = requiredSecondValidatorRole; }
    public String getFirstValidator() { return firstValidator; }
    public void setFirstValidator(String firstValidator) { this.firstValidator = firstValidator; }
    public String getFirstValidatedAt() { return firstValidatedAt; }
    public void setFirstValidatedAt(String firstValidatedAt) { this.firstValidatedAt = firstValidatedAt; }
    public String getFirstValidationNotes() { return firstValidationNotes; }
    public void setFirstValidationNotes(String firstValidationNotes) { this.firstValidationNotes = firstValidationNotes; }
    public String getSecondValidator() { return secondValidator; }
    public void setSecondValidator(String secondValidator) { this.secondValidator = secondValidator; }
    public String getSecondValidatedAt() { return secondValidatedAt; }
    public void setSecondValidatedAt(String secondValidatedAt) { this.secondValidatedAt = secondValidatedAt; }
    public String getSecondValidationNotes() { return secondValidationNotes; }
    public void setSecondValidationNotes(String secondValidationNotes) { this.secondValidationNotes = secondValidationNotes; }
    public boolean isRequiresAuthCode() { return requiresAuthCode; }
    public void setRequiresAuthCode(boolean requiresAuthCode) { this.requiresAuthCode = requiresAuthCode; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public String getRejectedBy() { return rejectedBy; }
    public void setRejectedBy(String rejectedBy) { this.rejectedBy = rejectedBy; }
    public String getRejectedAt() { return rejectedAt; }
    public void setRejectedAt(String rejectedAt) { this.rejectedAt = rejectedAt; }
    public List<ReceiptItemResponse> getItems() { return items; }
    public void setItems(List<ReceiptItemResponse> items) { this.items = items; }
}
