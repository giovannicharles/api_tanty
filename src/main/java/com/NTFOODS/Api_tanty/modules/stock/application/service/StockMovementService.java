package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockMovement;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockMovementType;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.jpa.StockMovementJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.repository.StockMovementRepository;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * StockMovementService - Service pour gérer les mouvements de stock
 */
@Service
@Transactional
public class StockMovementService {
    
    private static final Logger log = LoggerFactory.getLogger(StockMovementService.class);
    
    private final StockMovementRepository stockMovementRepository;
    private final StockItemService stockItemService;
    private final StockAlertService stockAlertService;
    
    public StockMovementService(StockMovementRepository stockMovementRepository, 
                               StockItemService stockItemService,
                               @Lazy StockAlertService stockAlertService) {
        this.stockMovementRepository = stockMovementRepository;
        this.stockItemService = stockItemService;
        this.stockAlertService = stockAlertService;
    }
    
    /**
     * Crée un nouveau mouvement de stock
     */
    public StockMovement createMovement(StockMovementType type, StockLocationId fromLocationId,
                                        StockLocationId toLocationId, Long productId, String productSku,
                                        String packagingType, BigDecimal quantity, BigDecimal quantityPerCarton,
                                        UserId requestedBy, String referenceNumber, String notes) {
        StockMovementJpaEntity entity = new StockMovementJpaEntity(
                type,
                fromLocationId != null ? fromLocationId.value() : null,
                toLocationId != null ? toLocationId.value() : null,
                productId,
                productSku,
                packagingType,
                quantity,
                quantityPerCarton,
                requestedBy != null ? UUID.nameUUIDFromBytes(requestedBy.getMatricule().getBytes()) : null,
                referenceNumber,
                notes
        );
        
        StockMovementJpaEntity saved = stockMovementRepository.save(entity);
        log.info("Created stock movement: {} for product {}", type, productSku);
        
        return mapToDomain(saved);
    }
    
    /**
     * Valide un mouvement de stock et met à jour les stocks
     */
    public void validateMovement(Long movementId, UserId validatedBy) {
        StockMovementJpaEntity entity = stockMovementRepository.findById(movementId)
                .orElseThrow(() -> new IllegalArgumentException("Movement not found: " + movementId));
        
        if (!"PENDING".equals(entity.getStatus())) {
            throw new IllegalStateException("Movement is not in PENDING status: " + entity.getStatus());
        }
        
        entity.setValidatedBy(validatedBy != null ? UUID.nameUUIDFromBytes(validatedBy.getMatricule().getBytes()) : null);
        entity.setValidatedAt(LocalDateTime.now());
        entity.setStatus("VALIDATED");
        
        // Update stock quantities based on movement type
        updateStockQuantities(entity, validatedBy);
        
        stockMovementRepository.save(entity);
        log.info("Validated stock movement: {}", movementId);

        // IMMEDIATE THRESHOLD CHECK: After stock update, check if the affected
        // product(s) are now below their thresholds and create alerts if needed.
        // This ensures alerts are generated immediately rather than waiting for
        // the hourly scheduled check.
        checkThresholdsAfterMovement(entity);
    }

    /**
     * Checks thresholds for the product(s) affected by a movement.
     * Called immediately after stock quantities are updated.
     */
    private void checkThresholdsAfterMovement(StockMovementJpaEntity movement) {
        try {
            if (movement.getToLocationId() != null) {
                StockLocationId toLoc = new StockLocationId(movement.getToLocationId());
                stockAlertService.checkSingleItemThresholds(toLoc, movement.getProductSku());
            }
            if (movement.getFromLocationId() != null) {
                StockLocationId fromLoc = new StockLocationId(movement.getFromLocationId());
                stockAlertService.checkSingleItemThresholds(fromLoc, movement.getProductSku());
            }
        } catch (Exception e) {
            log.warn("Failed to check thresholds after movement {}: {}", movement.getId(), e.getMessage());
        }
    }
    
    /**
     * Annule un mouvement de stock
     */
    public void cancelMovement(Long movementId, String reason) {
        StockMovementJpaEntity entity = stockMovementRepository.findById(movementId)
                .orElseThrow(() -> new IllegalArgumentException("Movement not found: " + movementId));
        
        if ("VALIDATED".equals(entity.getStatus())) {
            throw new IllegalStateException("Cannot cancel a validated movement");
        }
        
        entity.setStatus("CANCELLED");
        entity.setNotes((entity.getNotes() != null ? entity.getNotes() + "\n" : "") + "CANCELLED: " + reason);
        
        stockMovementRepository.save(entity);
        log.info("Cancelled stock movement: {} - Reason: {}", movementId, reason);
    }
    
    /**
     * Récupère tous les mouvements (tous statuts confondus)
     */
    public List<StockMovement> getAllMovements() {
        return stockMovementRepository.findAll().stream()
                .sorted((a, b) -> {
                    if (a.getRequestedAt() == null && b.getRequestedAt() == null) return 0;
                    if (a.getRequestedAt() == null) return 1;
                    if (b.getRequestedAt() == null) return -1;
                    return b.getRequestedAt().compareTo(a.getRequestedAt());
                })
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les mouvements en attente
     */
    public List<StockMovement> getPendingMovements() {
        return stockMovementRepository.findPendingMovements().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les mouvements d'une localisation
     */
    public List<StockMovement> getMovementsByLocation(StockLocationId locationId) {
        return stockMovementRepository.findByLocationIdOrderByRequestedAtDesc(locationId.value()).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les mouvements par type
     */
    public List<StockMovement> getMovementsByType(StockMovementType type) {
        return stockMovementRepository.findByType(type).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les mouvements par période
     */
    public List<StockMovement> getMovementsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return stockMovementRepository.findByRequestedAtBetween(startDate, endDate).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Met à jour les quantités de stock après validation d'un mouvement
     */
    private void updateStockQuantities(StockMovementJpaEntity movement, UserId validatedBy) {
        StockMovementType type = movement.getType();
        BigDecimal quantity = movement.getQuantity();
        
        switch (type) {
            case RECEPTION_PRODUCTION:
            case RECEPTION_CONSOMMABLE:
            case RECEPTION_RAW_MATERIAL:
            case RECEPTION_MATERIEL:
                // Ajout à l'emplacement de destination. Utilise la version upsert :
                // une réception peut concerner un produit jamais stocké en Stock Central
                // auparavant (aucun StockItem existant), ce que l'ancienne version
                // (addQuantity à 4 arguments, qui lève une exception si absent) ne
                // permettait pas de gérer.
                if (movement.getToLocationId() != null) {
                    stockItemService.addQuantity(
                            new StockLocationId(movement.getToLocationId()),
                            movement.getProductId(),
                            movement.getProductSku(),
                            movement.getPackagingType(),
                            movement.getQuantityPerCarton(),
                            quantity,
                            validatedBy
                    );
                }
                break;
                
            case TRANSFER_CENTRAL_TO_BUFFER:
            case TRANSFER_BUFFER_TO_MOBILE:
                // Subtract from source (by packagingType), add to destination (upsert)
                if (movement.getFromLocationId() != null) {
                    stockItemService.subtractQuantity(
                            new StockLocationId(movement.getFromLocationId()),
                            movement.getProductSku(),
                            movement.getPackagingType(),
                            quantity,
                            validatedBy
                    );
                }
                if (movement.getToLocationId() != null) {
                    stockItemService.addQuantity(
                            new StockLocationId(movement.getToLocationId()),
                            movement.getProductId(),
                            movement.getProductSku(),
                            movement.getPackagingType(),
                            movement.getQuantityPerCarton(),
                            quantity,
                            validatedBy
                    );
                }
                break;
                
            case TRANSFER_MOBILE_TO_CENTRAL:
                // Subtract from source (by packagingType), add to destination (upsert)
                if (movement.getFromLocationId() != null) {
                    stockItemService.subtractQuantity(
                            new StockLocationId(movement.getFromLocationId()),
                            movement.getProductSku(),
                            movement.getPackagingType(),
                            quantity,
                            validatedBy
                    );
                }
                if (movement.getToLocationId() != null) {
                    stockItemService.addQuantity(
                            new StockLocationId(movement.getToLocationId()),
                            movement.getProductId(),
                            movement.getProductSku(),
                            movement.getPackagingType(),
                            movement.getQuantityPerCarton(),
                            quantity,
                            validatedBy
                    );
                }
                break;
                
            case SALE:
                // Subtract from source
                if (movement.getFromLocationId() != null) {
                    stockItemService.subtractQuantity(
                            new StockLocationId(movement.getFromLocationId()),
                            movement.getProductSku(),
                            quantity,
                            validatedBy
                    );
                }
                break;
                
            case ADJUSTMENT:
                // Adjust based on positive or negative quantity
                if (movement.getToLocationId() != null) {
                    if (quantity.compareTo(BigDecimal.ZERO) >= 0) {
                        stockItemService.addQuantity(
                                new StockLocationId(movement.getToLocationId()),
                                movement.getProductSku(),
                                quantity,
                                validatedBy
                        );
                    } else {
                        stockItemService.subtractQuantity(
                                new StockLocationId(movement.getToLocationId()),
                                movement.getProductSku(),
                                quantity.abs(),
                                validatedBy
                        );
                    }
                }
                break;
                
            case LOSS:
            case EXPIRATION:
                // Subtract from source
                if (movement.getFromLocationId() != null) {
                    stockItemService.subtractQuantity(
                            new StockLocationId(movement.getFromLocationId()),
                            movement.getProductSku(),
                            quantity,
                            validatedBy
                    );
                }
                break;
                
            default:
                log.warn("Unhandled movement type: {}", type);
        }
    }
    
    private StockMovement mapToDomain(StockMovementJpaEntity entity) {
        StockMovement movement = new StockMovement(
                entity.getType(),
                entity.getFromLocationId() != null ? new StockLocationId(entity.getFromLocationId()) : null,
                entity.getToLocationId() != null ? new StockLocationId(entity.getToLocationId()) : null,
                entity.getProductId(),
                entity.getProductSku(),
                entity.getPackagingType(),
                entity.getQuantity(),
                entity.getQuantityPerCarton(),
                entity.getRequestedBy() != null ? new UserId(entity.getRequestedBy().toString()) : null,
                entity.getReferenceNumber(),
                entity.getNotes()
        );
        movement.setId(entity.getId());
        // Restore the original timestamps from the database — the constructor
        // sets requestedAt = now(), which overwrites the real value.
        movement.setRequestedAt(entity.getRequestedAt());
        if ("VALIDATED".equals(entity.getStatus()) && entity.getValidatedAt() != null) {
            movement.markValidated(
                    entity.getValidatedBy() != null ? new UserId(entity.getValidatedBy().toString()) : null,
                    entity.getValidatedAt()
            );
        } else {
            movement.setStatus(entity.getStatus());
        }
        return movement;
    }
}
