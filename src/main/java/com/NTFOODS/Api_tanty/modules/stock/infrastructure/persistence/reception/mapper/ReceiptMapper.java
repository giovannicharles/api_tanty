package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.reception.mapper;

import com.NTFOODS.Api_tanty.modules.stock.domain.reception.aggregate.ReceiptAggregate;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.entity.ReceiptItem;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceiptStatus;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceptionType;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject.ReceiptNumber;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.reception.jpa.ReceiptItemJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.reception.jpa.ReceiptJpaEntity;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import java.util.ArrayList;
import java.util.List;

/**
 * ReceiptMapper - Traduction ReceiptAggregate (domaine) <-> ReceiptJpaEntity (persistance).
 * Réécrit entièrement : l'ancienne version référençait des champs inexistants sur
 * l'entité JPA (ex: getRejectionReason) et ne mappait aucune ligne d'article, ce qui
 * empêchait la compilation et rendait la persistance des réceptions impossible.
 * La reconstruction domaine utilise ReceiptAggregate.hydrate() : aucune règle métier
 * n'est rejouée, contrairement à l'ancien pattern qui appelait firstValidate()/
 * secondValidate() lors de la lecture et écrasait les horodatages historiques par
 * la date du jour.
 */
public final class ReceiptMapper {

    private ReceiptMapper() {}

    public static ReceiptJpaEntity toEntity(ReceiptAggregate aggregate) {
        ReceiptJpaEntity entity = new ReceiptJpaEntity();
        entity.setId(aggregate.getId());
        entity.setReceiptNumber(aggregate.getReceiptNumber().getValue());
        entity.setReceptionType(aggregate.getReceptionType().name());
        entity.setSourceLabel(aggregate.getSourceLabel());
        entity.setSourceId(aggregate.getSourceId());
        entity.setReceiptDate(aggregate.getReceiptDate());
        entity.setDestinationLocationId(aggregate.getDestinationLocationId());
        entity.setCreatedBy(aggregate.getCreatedBy() != null ? aggregate.getCreatedBy().getMatricule() : null);
        entity.setStatus(aggregate.getStatus().name());
        entity.setFirstValidator(aggregate.getFirstValidator() != null ? aggregate.getFirstValidator().getMatricule() : null);
        entity.setFirstValidatedAt(aggregate.getFirstValidatedAt());
        entity.setFirstValidationNotes(aggregate.getFirstValidationNotes());
        entity.setSecondValidator(aggregate.getSecondValidator() != null ? aggregate.getSecondValidator().getMatricule() : null);
        entity.setSecondValidatedAt(aggregate.getSecondValidatedAt());
        entity.setSecondValidationNotes(aggregate.getSecondValidationNotes());
        entity.setRequiresAuthCode(aggregate.isRequiresAuthCode());
        entity.setRejectionReason(aggregate.getRejectionReason());
        entity.setRejectedBy(aggregate.getRejectedBy() != null ? aggregate.getRejectedBy().getMatricule() : null);
        entity.setRejectedAt(aggregate.getRejectedAt());

        List<ReceiptItemJpaEntity> itemEntities = new ArrayList<>();
        for (ReceiptItem item : aggregate.getItems()) {
            ReceiptItemJpaEntity itemEntity = new ReceiptItemJpaEntity();
            itemEntity.setId(item.getId());
            itemEntity.setReceipt(entity);
            itemEntity.setProductId(item.getProductId().getValue());
            itemEntity.setProductName(item.getProductName());
            itemEntity.setProductSku(item.getProductSku());
            itemEntity.setPackagingType(item.getPackagingType());
            itemEntity.setQuantityPerCarton(item.getQuantityPerCarton());
            itemEntity.setOrderedQty(item.getOrderedQty().getValue());
            itemEntity.setUnit(item.getOrderedQty().getUnit());
            itemEntity.setReceivedQty(item.getReceivedQty().getValue());
            itemEntity.setLotNumber(item.getLotNumber());
            itemEntity.setDeviationReason(item.getDeviationReason());
            itemEntities.add(itemEntity);
        }
        entity.setItems(itemEntities);
        return entity;
    }

    public static ReceiptAggregate toDomain(ReceiptJpaEntity entity) {
        List<ReceiptItem> items = new ArrayList<>();
        for (ReceiptItemJpaEntity itemEntity : entity.getItems()) {
            Quantity ordered = new Quantity(itemEntity.getOrderedQty(), itemEntity.getUnit());
            Quantity received = new Quantity(itemEntity.getReceivedQty(), itemEntity.getUnit());
            items.add(ReceiptItem.hydrate(
                    itemEntity.getId(),
                    new ProductId(itemEntity.getProductId()),
                    itemEntity.getProductName(),
                    itemEntity.getProductSku(),
                    itemEntity.getPackagingType(),
                    itemEntity.getQuantityPerCarton(),
                    ordered,
                    received,
                    itemEntity.getLotNumber(),
                    itemEntity.getDeviationReason()
            ));
        }

        return ReceiptAggregate.hydrate(
                entity.getId(),
                new ReceiptNumber(entity.getReceiptNumber()),
                ReceptionType.valueOf(entity.getReceptionType()),
                entity.getSourceLabel(),
                entity.getSourceId(),
                entity.getReceiptDate(),
                entity.getDestinationLocationId(),
                entity.getCreatedBy() != null ? new UserId(entity.getCreatedBy()) : null,
                items,
                ReceiptStatus.valueOf(entity.getStatus()),
                entity.getFirstValidator() != null ? new UserId(entity.getFirstValidator()) : null,
                entity.getFirstValidatedAt(),
                entity.getFirstValidationNotes(),
                entity.getSecondValidator() != null ? new UserId(entity.getSecondValidator()) : null,
                entity.getSecondValidatedAt(),
                entity.getSecondValidationNotes(),
                entity.isRequiresAuthCode(),
                entity.getRejectionReason(),
                entity.getRejectedBy() != null ? new UserId(entity.getRejectedBy()) : null,
                entity.getRejectedAt()
        );
    }
}
