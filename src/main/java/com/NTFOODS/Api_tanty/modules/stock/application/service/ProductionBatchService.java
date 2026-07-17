package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockMovementType;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockMovement;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockLocation;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.productionbatch.jpa.ProductionBatchJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.productionbatch.repository.ProductionBatchRepository;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ProductionBatchService {

    private static final Logger log = LoggerFactory.getLogger(ProductionBatchService.class);

    private final ProductionBatchRepository batchRepository;
    private final StockMovementService stockMovementService;
    private final StockLocationService stockLocationService;

    public ProductionBatchService(ProductionBatchRepository batchRepository,
                                   StockMovementService stockMovementService,
                                   StockLocationService stockLocationService) {
        this.batchRepository = batchRepository;
        this.stockMovementService = stockMovementService;
        this.stockLocationService = stockLocationService;
    }

    public List<ProductionBatchJpaEntity> getAllBatches() {
        return batchRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<ProductionBatchJpaEntity> getPendingBatches() {
        return batchRepository.findByStatusOrderByCreatedAtDesc("DECLARED_BY_PRODUCTION");
    }

    public ProductionBatchJpaEntity getBatchById(Long id) {
        return batchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lot de production introuvable: " + id));
    }

    public ProductionBatchJpaEntity declareBatch(Long productId, String productSku, String productName,
                                                  String productUnit, BigDecimal declaredQuantityKg,
                                                  BigDecimal equivalentUnits, LocalDate productionDate,
                                                  String declaredBy, String declaredByName, String notes,
                                                  String conditioningType, BigDecimal conditioningQty) {
        ProductionBatchJpaEntity batch = new ProductionBatchJpaEntity();
        batch.setProductId(productId);
        batch.setProductSku(productSku);
        batch.setProductName(productName);
        batch.setProductUnit(productUnit);
        batch.setDeclaredQuantityKg(declaredQuantityKg);
        batch.setEquivalentUnits(equivalentUnits);
        batch.setProductionDate(productionDate);
        batch.setBatchDate(productionDate);
        batch.setStatus("DECLARED_BY_PRODUCTION");
        batch.setDeclaredBy(declaredBy);
        batch.setDeclaredByName(declaredByName);
        batch.setCreatedAt(LocalDateTime.now());
        batch.setNotes(notes);
        batch.setConditioningType(conditioningType);
        batch.setConditioningQty(conditioningQty);

        ProductionBatchJpaEntity saved = batchRepository.save(batch);
        log.info("Lot de production déclaré: {} par {}", saved.getId(), declaredBy);
        return saved;
    }

    public ProductionBatchJpaEntity validateBatch(Long id, UserId validator, String validatorName, String notes) {
        ProductionBatchJpaEntity batch = getBatchById(id);
        if (!"DECLARED_BY_PRODUCTION".equals(batch.getStatus())) {
            throw new IllegalStateException("Seul un lot déclaré peut être validé (statut actuel: " + batch.getStatus() + ")");
        }

        batch.setStatus("VALIDATED_BY_STOCK");
        batch.setStockValidator(validator.getMatricule());
        batch.setStockValidatorName(validatorName);
        batch.setValidatedAt(LocalDateTime.now());
        if (notes != null && !notes.isBlank()) {
            batch.setNotes((batch.getNotes() != null ? batch.getNotes() + " | " : "") + notes);
        }

        ProductionBatchJpaEntity saved = batchRepository.save(batch);

        StockLocation centralLocation = stockLocationService.getLocationsByType(StockLocationType.STOCK_CENTRAL)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Aucun emplacement Stock Central configuré"));

        StockLocationId destinationId = centralLocation.getId();

        StockMovement movement = stockMovementService.createMovement(
                StockMovementType.RECEPTION_PRODUCTION,
                null,
                destinationId,
                batch.getProductId(),
                batch.getProductSku(),
                batch.getConditioningType() != null ? batch.getConditioningType() : "CARTON",
                batch.getDeclaredQuantityKg(),
                batch.getConditioningQty() != null ? batch.getConditioningQty() : BigDecimal.ONE,
                validator,
                "LOT-PROD-" + batch.getId(),
                "Entrée stock suite validation lot de production " + batch.getId()
        );

        stockMovementService.validateMovement(movement.getId(), validator);

        log.info("Lot de production {} validé par {} - stock mis à jour", id, validator.getMatricule());
        return saved;
    }

    public ProductionBatchJpaEntity rejectBatch(Long id, UserId rejectedBy, String reason) {
        ProductionBatchJpaEntity batch = getBatchById(id);
        if (!"DECLARED_BY_PRODUCTION".equals(batch.getStatus())) {
            throw new IllegalStateException("Seul un lot déclaré peut être rejeté (statut actuel: " + batch.getStatus() + ")");
        }

        batch.setStatus("REJECTED");
        batch.setNotes((batch.getNotes() != null ? batch.getNotes() + " | " : "") + "REJETÉ: " + reason);

        ProductionBatchJpaEntity saved = batchRepository.save(batch);
        log.info("Lot de production {} rejeté par {}", id, rejectedBy.getMatricule());
        return saved;
    }
}
