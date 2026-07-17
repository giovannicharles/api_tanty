package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.reception.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * ReceiptItemJpaEntity - Table stock_receipt_items.
 * Porte les informations de conditionnement (§2 du cahier des charges) : type
 * d'emballage, quantité par carton, numéro de lot.
 */
@Entity
@Table(name = "stock_receipt_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", nullable = false)
    private ReceiptJpaEntity receipt;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false, length = 150)
    private String productName;

    @Column(nullable = false, length = 40)
    private String productSku;

    @Column(length = 30)
    private String packagingType;

    private BigDecimal quantityPerCarton;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal orderedQty;

    @Column(nullable = false, length = 20)
    private String unit;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal receivedQty;

    @Column(length = 40)
    private String lotNumber;

    @Column(length = 300)
    private String deviationReason;
}
