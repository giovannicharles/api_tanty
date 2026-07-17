package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.reception.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ReceiptJpaEntity - Table stock_receipts.
 * Réécrite pour porter le type de réception (CONSOMMABLE/MATIERE_PREMIERE/MATERIEL)
 * et les informations de rejet, absentes de l'ancienne version (qui provoquait des
 * erreurs de compilation dans ReceiptMapper : champs référencés mais inexistants).
 */
@Entity
@Table(name = "stock_receipts", indexes = {
        @Index(name = "idx_receipt_number", columnList = "receiptNumber", unique = true),
        @Index(name = "idx_receipt_status", columnList = "status"),
        @Index(name = "idx_receipt_type", columnList = "receptionType")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String receiptNumber;

    @Column(nullable = false, length = 30)
    private String receptionType;

    @Column(length = 150)
    private String sourceLabel;

    private Long sourceId;

    @Column(nullable = false)
    private LocalDateTime receiptDate;

    @Column(nullable = false)
    private java.util.UUID destinationLocationId;

    @Column(nullable = false, length = 30)
    private String createdBy;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(length = 30)
    private String firstValidator;
    private LocalDateTime firstValidatedAt;
    @Column(length = 500)
    private String firstValidationNotes;

    @Column(length = 30)
    private String secondValidator;
    private LocalDateTime secondValidatedAt;
    @Column(length = 500)
    private String secondValidationNotes;

    private boolean requiresAuthCode;

    @Column(length = 500)
    private String rejectionReason;
    @Column(length = 30)
    private String rejectedBy;
    private LocalDateTime rejectedAt;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ReceiptItemJpaEntity> items = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
