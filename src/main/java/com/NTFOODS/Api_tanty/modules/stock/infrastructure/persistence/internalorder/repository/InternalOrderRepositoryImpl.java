package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.internalorder.repository;

import com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.aggregate.InternalOrderAggregate;
import com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.entity.InternalOrderItem;
import com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.enums.InternalOrderStatus;
import com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.repository.InternalOrderRepository;
import com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.valueobject.InternalOrderNumber;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.internalorder.jpa.InternalOrderItemJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.internalorder.jpa.InternalOrderJpaEntity;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** Adapter: mappe entre l'aggregate domaine et les entités JPA. */
@Component
public class InternalOrderRepositoryImpl implements InternalOrderRepository {

    private final InternalOrderJpaRepository jpaRepository;

    public InternalOrderRepositoryImpl(InternalOrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    // ── Aggregate → JPA ──────────────────────────────────────

    @Override
    public InternalOrderAggregate save(InternalOrderAggregate aggregate) {
        InternalOrderJpaEntity entity = toJpaEntity(aggregate);
        InternalOrderJpaEntity saved = jpaRepository.save(entity);
        return toAggregate(saved);
    }

    @Override
    public Optional<InternalOrderAggregate> findById(Long id) {
        return jpaRepository.findById(id).map(this::toAggregate);
    }

    @Override
    public List<InternalOrderAggregate> findAll() {
        return jpaRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<InternalOrderAggregate> findActive() {
        return jpaRepository.findByStatusInOrderByCreatedAtDesc(
                List.of("DRAFT", "APPROVED", "PARTIALLY_DELIVERED")
        ).stream().map(this::toAggregate).collect(Collectors.toList());
    }

    @Override
    public List<InternalOrderAggregate> findByStatus(String status) {
        return jpaRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(this::toAggregate)
                .collect(Collectors.toList());
    }

    // ── Mapping methods ──────────────────────────────────────

    private InternalOrderJpaEntity toJpaEntity(InternalOrderAggregate agg) {
        InternalOrderJpaEntity entity = new InternalOrderJpaEntity();
        entity.setOrderNumber(agg.getOrderNumber().getValue());
        entity.setOrderDate(agg.getOrderDate());
        entity.setStatus(agg.getStatus().name());
        entity.setRequestedBy(agg.getRequestedBy().getMatricule());
        entity.setRequestedByName(agg.getRequestedByName());
        entity.setCreatedAt(agg.getCreatedAt());
        entity.setNotes(agg.getNotes());

        if (agg.getApprovedBy() != null) {
            entity.setApprovedBy(agg.getApprovedBy().getMatricule());
            entity.setApprovedByName(agg.getApprovedByName());
            entity.setApprovedAt(agg.getApprovedAt());
        }
        if (agg.getCancelledBy() != null) {
            entity.setCancelledBy(agg.getCancelledBy().getMatricule());
            entity.setCancelledReason(agg.getCancelledReason());
            entity.setCancelledAt(agg.getCancelledAt());
        }

        for (InternalOrderItem item : agg.getItems()) {
            InternalOrderItemJpaEntity itemEntity = new InternalOrderItemJpaEntity();
            itemEntity.setProductId(item.getProductId().getValue());
            itemEntity.setProductSku(item.getProductSku());
            itemEntity.setProductName(item.getProductName());
            itemEntity.setProductUnit(item.getProductUnit());
            itemEntity.setPackagingType(item.getPackagingType());
            itemEntity.setQuantityPerCarton(item.getQuantityPerCarton());
            itemEntity.setRequestedQty(item.getRequestedQty().getValue());
            itemEntity.setDeliveredQty(item.getDeliveredQty().getValue());
            itemEntity.setNotes(item.getNotes());
            entity.addItem(itemEntity);
        }

        return entity;
    }

    private InternalOrderAggregate toAggregate(InternalOrderJpaEntity entity) {
        List<InternalOrderItem> items = new ArrayList<>();
        for (InternalOrderItemJpaEntity ie : entity.getItems()) {
            String unit = ie.getProductUnit() != null ? ie.getProductUnit() : "unite";
            items.add(new InternalOrderItem(
                    new ProductId(ie.getProductId()),
                    new Quantity(ie.getRequestedQty(), unit),
                    ie.getProductSku(),
                    ie.getProductName(),
                    ie.getProductUnit(),
                    ie.getPackagingType(),
                    ie.getQuantityPerCarton(),
                    ie.getNotes()
            ));
            // Restore deliveredQty
            if (ie.getDeliveredQty() != null && ie.getDeliveredQty().compareTo(BigDecimal.ZERO) > 0) {
                InternalOrderItem lastItem = items.get(items.size() - 1);
                lastItem.addDeliveredQty(new Quantity(ie.getDeliveredQty(), unit));
            }
        }

        return InternalOrderAggregate.reconstitute(
                new InternalOrderNumber(entity.getOrderNumber()),
                entity.getOrderDate(),
                InternalOrderStatus.valueOf(entity.getStatus()),
                new UserId(entity.getRequestedBy()),
                entity.getRequestedByName(),
                entity.getApprovedBy() != null ? new UserId(entity.getApprovedBy()) : null,
                entity.getApprovedByName(),
                entity.getApprovedAt(),
                entity.getCancelledBy() != null ? new UserId(entity.getCancelledBy()) : null,
                entity.getCancelledReason(),
                entity.getCancelledAt(),
                entity.getCreatedAt(),
                entity.getNotes(),
                items
        );
    }
}
