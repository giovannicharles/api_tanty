package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_prices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private String priceType;
    private BigDecimal price;
    private String currency;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private BigDecimal minQuantity;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
