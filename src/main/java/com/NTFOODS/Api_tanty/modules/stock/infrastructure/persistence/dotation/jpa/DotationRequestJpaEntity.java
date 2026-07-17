package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.dotation.jpa;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DotationRequestJpaEntity - Entité JPA pour DotationRequest
 */
@Entity
@Table(name = "dotation_requests")
public class DotationRequestJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "commercial_id", nullable = false)
    private String commercialId;
    
    @Column(name = "commercial_matricule", nullable = false)
    private String commercialMatricule;
    
    @Column(name = "commercial_name", nullable = false)
    private String commercialName;
    
    @Column(name = "justification", columnDefinition = "TEXT")
    private String justification;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    @Column(name = "payment_verified_by")
    private String paymentVerifiedBy;
    
    @Column(name = "payment_verified_at")
    private LocalDateTime paymentVerifiedAt;
    
    @Column(name = "quantity_validated_by")
    private String quantityValidatedBy;
    
    @Column(name = "quantity_validated_at")
    private LocalDateTime quantityValidatedAt;
    
    @Column(name = "quantity_validation_comments", columnDefinition = "TEXT")
    private String quantityValidationComments;
    
    @Column(name = "reviewed_by")
    private String reviewedBy;
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @Column(name = "review_comments", columnDefinition = "TEXT")
    private String reviewComments;
    
    @Column(name = "approved_by")
    private String approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "delivered_by")
    private String deliveredBy;
    
    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;
    
    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "reference_number", unique = true, nullable = false)
    private String referenceNumber;
    
    @OneToMany(mappedBy = "dotationRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DotationItemJpaEntity> items = new ArrayList<>();
    
    public DotationRequestJpaEntity() {}
    
    public DotationRequestJpaEntity(String commercialId, String commercialMatricule, String commercialName,
                                   String justification, LocalDateTime scheduledDate, String referenceNumber) {
        this.commercialId = commercialId;
        this.commercialMatricule = commercialMatricule;
        this.commercialName = commercialName;
        this.justification = justification;
        this.scheduledDate = scheduledDate;
        this.referenceNumber = referenceNumber;
        this.status = "PENDING";
        this.requestedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCommercialId() { return commercialId; }
    public void setCommercialId(String commercialId) { this.commercialId = commercialId; }
    
    public String getCommercialMatricule() { return commercialMatricule; }
    public void setCommercialMatricule(String commercialMatricule) { this.commercialMatricule = commercialMatricule; }
    
    public String getCommercialName() { return commercialName; }
    public void setCommercialName(String commercialName) { this.commercialName = commercialName; }
    
    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPaymentVerifiedBy() { return paymentVerifiedBy; }
    public void setPaymentVerifiedBy(String paymentVerifiedBy) { this.paymentVerifiedBy = paymentVerifiedBy; }
    
    public LocalDateTime getPaymentVerifiedAt() { return paymentVerifiedAt; }
    public void setPaymentVerifiedAt(LocalDateTime paymentVerifiedAt) { this.paymentVerifiedAt = paymentVerifiedAt; }
    
    public String getQuantityValidatedBy() { return quantityValidatedBy; }
    public void setQuantityValidatedBy(String quantityValidatedBy) { this.quantityValidatedBy = quantityValidatedBy; }
    
    public LocalDateTime getQuantityValidatedAt() { return quantityValidatedAt; }
    public void setQuantityValidatedAt(LocalDateTime quantityValidatedAt) { this.quantityValidatedAt = quantityValidatedAt; }
    
    public String getQuantityValidationComments() { return quantityValidationComments; }
    public void setQuantityValidationComments(String quantityValidationComments) { this.quantityValidationComments = quantityValidationComments; }
    
    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
    
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    
    public String getReviewComments() { return reviewComments; }
    public void setReviewComments(String reviewComments) { this.reviewComments = reviewComments; }
    
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    
    public String getDeliveredBy() { return deliveredBy; }
    public void setDeliveredBy(String deliveredBy) { this.deliveredBy = deliveredBy; }
    
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    
    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public List<DotationItemJpaEntity> getItems() { return items; }
    public void setItems(List<DotationItemJpaEntity> items) { this.items = items; }
    
    public void addItem(DotationItemJpaEntity item) {
        this.items.add(item);
        item.setDotationRequest(this);
    }
}
