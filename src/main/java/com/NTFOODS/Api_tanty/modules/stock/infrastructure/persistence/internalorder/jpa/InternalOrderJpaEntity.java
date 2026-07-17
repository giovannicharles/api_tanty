package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.internalorder.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stock_internal_orders", indexes = {
        @Index(name = "idx_int_order_status", columnList = "status"),
        @Index(name = "idx_int_order_date", columnList = "orderDate")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternalOrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private LocalDate orderDate;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String requestedBy;
    private String requestedByName;

    private String approvedBy;
    private String approvedByName;
    private LocalDateTime approvedAt;

    private String cancelledBy;
    private String cancelledReason;
    private LocalDateTime cancelledAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private String notes;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<InternalOrderItemJpaEntity> items = new ArrayList<>();

    public void addItem(InternalOrderItemJpaEntity item) {
        item.setOrder(this);
        this.items.add(item);
    }
}
