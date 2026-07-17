package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.internalorder.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "stock_internal_order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternalOrderItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    private String productSku;
    private String productName;
    private String productUnit;
    private String packagingType;
    private BigDecimal quantityPerCarton;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal requestedQty;

    @Column(precision = 19, scale = 3)
    private BigDecimal deliveredQty;

    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId")
    private InternalOrderJpaEntity order;

    public boolean isFullyDelivered() {
        return deliveredQty != null && deliveredQty.compareTo(requestedQty) >= 0;
    }
}
