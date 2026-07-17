package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.productionbatch.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_production_batches", indexes = {
        @Index(name = "idx_prod_batch_status", columnList = "status"),
        @Index(name = "idx_prod_batch_product", columnList = "productId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionBatchJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    private String productSku;
    private String productName;
    private String productUnit;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal declaredQuantityKg;

    private BigDecimal equivalentUnits;

    @Column(nullable = false)
    private LocalDate productionDate;

    private LocalDate batchDate;

    @Column(nullable = false)
    private String status;

    private String declaredBy;
    private String declaredByName;
    private String stockValidator;
    private String stockValidatorName;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime validatedAt;
    private String notes;

    // Conditionnement (comment le lot a été conditionné par la production)
    private String conditioningType;  // CARTON, SEAU_1L, SEAU_5L, SEAU_10L, GAINE, SAC, KG
    private BigDecimal conditioningQty; // Nombre d'unités de conditionnement
}
