package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantJpaEntity {
    @Id
    private Long id;
    private Long productLineId;
    private String name;
    private String code;
    // getters/setters
}