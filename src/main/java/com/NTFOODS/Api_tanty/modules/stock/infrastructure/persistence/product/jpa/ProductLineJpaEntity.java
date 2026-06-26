package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_lines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductLineJpaEntity {
    @Id
    private Long id;
    private Long brandId;
    private String name;
    private String code;
    private boolean active;
    // getters/setters
}