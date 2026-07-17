package com.NTFOODS.Api_tanty.modules.stock.domain.dotation.entity;

import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DotationRequest - Entité représentant une demande de dotation
 * Workflow: Commercial soumet → Secrétaire vérifie paiement → Comptable valide quantités → Gestionnaire stock approuve → Livraison
 */
public class DotationRequest {
    
    private Long id;
    private final UserId commercialId;
    private final String commercialMatricule;
    private final String commercialName;
    private List<DotationItem> items;
    private String justification;
    private String status; // PENDING, PAYMENT_VERIFIED, QUANTITY_VALIDATED, APPROVED, REJECTED, COMPLETED
    private UserId paymentVerifiedBy;
    private LocalDateTime paymentVerifiedAt;
    private UserId quantityValidatedBy;
    private LocalDateTime quantityValidatedAt;
    private String quantityValidationComments;
    private UserId reviewedBy;
    private LocalDateTime reviewedAt;
    private String reviewComments;
    private UserId approvedBy;
    private LocalDateTime approvedAt;
    private UserId deliveredBy;
    private LocalDateTime requestedAt;
    private LocalDateTime scheduledDate; // Date prévue pour la dotation (souvent le lendemain)
    private LocalDateTime completedAt;
    private String referenceNumber;
    
    public DotationRequest(UserId commercialId, String commercialMatricule, String commercialName, 
                          String justification, LocalDateTime scheduledDate) {
        this.commercialId = commercialId;
        this.commercialMatricule = commercialMatricule;
        this.commercialName = commercialName;
        this.justification = justification;
        this.scheduledDate = scheduledDate;
        this.items = new ArrayList<>();
        this.status = "PENDING";
        this.requestedAt = LocalDateTime.now();
        this.referenceNumber = generateReferenceNumber();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public UserId getCommercialId() {
        return commercialId;
    }
    
    public String getCommercialMatricule() {
        return commercialMatricule;
    }
    
    public String getCommercialName() {
        return commercialName;
    }
    
    public List<DotationItem> getItems() {
        return items;
    }
    
    public void setItems(List<DotationItem> items) {
        this.items = items;
    }
    
    public void addItem(DotationItem item) {
        this.items.add(item);
    }
    
    public String getJustification() {
        return justification;
    }
    
    public void setJustification(String justification) {
        this.justification = justification;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public UserId getPaymentVerifiedBy() {
        return paymentVerifiedBy;
    }
    
    public LocalDateTime getPaymentVerifiedAt() {
        return paymentVerifiedAt;
    }
    
    public UserId getQuantityValidatedBy() {
        return quantityValidatedBy;
    }
    
    public LocalDateTime getQuantityValidatedAt() {
        return quantityValidatedAt;
    }
    
    public String getQuantityValidationComments() {
        return quantityValidationComments;
    }
    
    public UserId getReviewedBy() {
        return reviewedBy;
    }
    
    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }
    
    public String getReviewComments() {
        return reviewComments;
    }
    
    public UserId getApprovedBy() {
        return approvedBy;
    }
    
    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
    
    public UserId getDeliveredBy() {
        return deliveredBy;
    }
    
    public void setDeliveredBy(UserId deliveredBy) {
        this.deliveredBy = deliveredBy;
    }
    
    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }
    
    public LocalDateTime getScheduledDate() {
        return scheduledDate;
    }
    
    public void setScheduledDate(LocalDateTime scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    
    /**
     * La secrétaire vérifie que le commercial a déposé l'argent du précédent
     */
    public void verifyPayment(UserId verifiedBy) {
        if (!"PENDING".equals(this.status)) {
            throw new IllegalStateException("Seules les demandes PENDING peuvent être vérifiées pour le paiement");
        }
        this.paymentVerifiedBy = verifiedBy;
        this.paymentVerifiedAt = LocalDateTime.now();
        this.status = "PAYMENT_VERIFIED";
    }
    
    /**
     * Le comptable arbitre et valide les quantités (peut modifier les items)
     */
    public void validateQuantities(UserId validatedBy, String comments, List<DotationItem> modifiedItems) {
        if (!"PAYMENT_VERIFIED".equals(this.status)) {
            throw new IllegalStateException("Seules les demandes PAYMENT_VERIFIED peuvent être validées par le comptable");
        }
        this.quantityValidatedBy = validatedBy;
        this.quantityValidatedAt = LocalDateTime.now();
        this.quantityValidationComments = comments;
        if (modifiedItems != null && !modifiedItems.isEmpty()) {
            this.items = modifiedItems;
        }
        this.status = "QUANTITY_VALIDATED";
    }
    
    /**
     * Le gestionnaire de stock approuve la demande (décide de livrer)
     */
    public void approve(UserId approvedBy) {
        if (!"QUANTITY_VALIDATED".equals(this.status)) {
            throw new IllegalStateException("Seules les demandes QUANTITY_VALIDATED peuvent être approuvées");
        }
        this.approvedBy = approvedBy;
        this.approvedAt = LocalDateTime.now();
        this.reviewedBy = approvedBy;
        this.reviewedAt = LocalDateTime.now();
        this.status = "APPROVED";
    }
    
    /**
     * Rejet possible à toute étape avant approbation
     */
    public void reject(UserId rejectedBy, String reason) {
        if ("COMPLETED".equals(this.status) || "REJECTED".equals(this.status)) {
            throw new IllegalStateException("Cette demande ne peut plus être rejetée");
        }
        this.reviewedBy = rejectedBy;
        this.reviewedAt = LocalDateTime.now();
        this.reviewComments = reason;
        this.status = "REJECTED";
    }
    
    /**
     * Marque la demande comme complétée après transfert de stock — le gestionnaire a donné les produits
     */
    public void complete(UserId deliveredBy) {
        if (!"APPROVED".equals(this.status)) {
            throw new IllegalStateException("Can only complete APPROVED requests");
        }
        this.deliveredBy = deliveredBy;
        this.completedAt = LocalDateTime.now();
        this.status = "COMPLETED";
    }
    
    private String generateReferenceNumber() {
        return "DOT-" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }
    
    /**
     * DotationItem - Item dans une demande de dotation
     */
    public static class DotationItem {
        private Long id;
        private final Long productId;
        private final String productSku;
        private final String productName;
        private final String packagingType;
        private BigDecimal requestedQuantity;
        private BigDecimal approvedQuantity;
        private BigDecimal quantityPerCarton;
        private String notes;
        
        public DotationItem(Long productId, String productSku, String productName, 
                           String packagingType, BigDecimal requestedQuantity, 
                           BigDecimal quantityPerCarton) {
            this.productId = productId;
            this.productSku = productSku;
            this.productName = productName;
            this.packagingType = packagingType;
            this.requestedQuantity = requestedQuantity;
            this.quantityPerCarton = quantityPerCarton;
            this.approvedQuantity = requestedQuantity; // Par défaut, approuvé = demandé
        }
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public Long getProductId() {
            return productId;
        }
        
        public String getProductSku() {
            return productSku;
        }
        
        public String getProductName() {
            return productName;
        }
        
        public String getPackagingType() {
            return packagingType;
        }
        
        public BigDecimal getRequestedQuantity() {
            return requestedQuantity;
        }
        
        public void setRequestedQuantity(BigDecimal requestedQuantity) {
            this.requestedQuantity = requestedQuantity;
        }
        
        public BigDecimal getApprovedQuantity() {
            return approvedQuantity;
        }
        
        public void setApprovedQuantity(BigDecimal approvedQuantity) {
            this.approvedQuantity = approvedQuantity;
        }
        
        public BigDecimal getQuantityPerCarton() {
            return quantityPerCarton;
        }
        
        public String getNotes() {
            return notes;
        }
        
        public void setNotes(String notes) {
            this.notes = notes;
        }
    }
}
