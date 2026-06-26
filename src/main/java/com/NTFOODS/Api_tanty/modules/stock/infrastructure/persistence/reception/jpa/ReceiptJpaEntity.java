package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.reception.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "receipts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String receiptNumber;

    @Column(nullable = false)
    private String source;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "source_name")
    private String sourceName;

    @Column(nullable = false)
    private String receiptDate;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "warehouse_name")
    private String warehouseName;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "first_validator_id")
    private Long firstValidatorId;

    @Column(name = "first_validator_name")
    private String firstValidatorName;

    @Column(name = "first_validated_at")
    private String firstValidatedAt;

    @Column(name = "first_validator_notes")
    private String firstValidatorNotes;

    @Column(name = "second_validator_id")
    private Long secondValidatorId;

    @Column(name = "second_validator_name")
    private String secondValidatorName;

    @Column(name = "second_validated_at")
    private String secondValidatedAt;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceiptItemJpaEntity> items;
}
