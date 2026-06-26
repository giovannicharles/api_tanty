package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductJpaEntity {
    @Id
    private Long id;
    private String sku;
    private Long variantId;
    private String barcode;
    private String category;
    private String unit;
    private BigDecimal unitPriceAmount;
    private Integer leadTimeDays;
    private Integer safetyStockDays;
    private boolean active;
    
    // Conditionnement fields
    private String packagingType; // SACHET, ETUI, SEAU, BOUTEILLE, DOYPACK, BOITE
    private Integer quantityPerCarton; // Number of units per carton
    private BigDecimal unitWeight; // Weight per unit in grams
    private String volume; // Volume for liquids (e.g., "1L", "2L", "5L")
    private Integer cartonsPerAssortiment; // For assorti cartons

    // getters/setters
}