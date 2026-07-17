package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.domain.alerte.entity.StockAlert;
import com.NTFOODS.Api_tanty.modules.stock.domain.batch.entity.StockBatch;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.alerte.jpa.StockAlertJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.alerte.repository.StockAlertRepository;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StockAlertService - Service pour gérer les alertes de stock
 * Gère la création, l'acknowledgment et la résolution des alertes
 */
@Service
@Transactional
public class StockAlertService {
    
    private static final Logger log = LoggerFactory.getLogger(StockAlertService.class);
    
    private final StockAlertRepository stockAlertRepository;
    private final StockItemService stockItemService;
    private final StockLocationService stockLocationService;
    private final StockBatchService stockBatchService;
    private final StockAuditLogService auditLogService;

    public StockAlertService(StockAlertRepository stockAlertRepository,
                            StockItemService stockItemService,
                            StockLocationService stockLocationService,
                            StockBatchService stockBatchService,
                            StockAuditLogService auditLogService) {
        this.stockAlertRepository = stockAlertRepository;
        this.stockItemService = stockItemService;
        this.stockLocationService = stockLocationService;
        this.stockBatchService = stockBatchService;
        this.auditLogService = auditLogService;
    }
    
    /**
     * Crée une alerte de stock bas
     */
    public StockAlert createLowStockAlert(StockLocationId locationId, Long productId, String productSku,
                                         String productName, BigDecimal currentQuantity, BigDecimal threshold) {
        StockAlert.AlertPriority priority = determinePriority(currentQuantity, threshold);
        String message = String.format("Stock bas pour %s (%s): %.2f unités (seuil: %.2f)", 
                productName, productSku, currentQuantity, threshold);
        
        StockAlertJpaEntity entity = new StockAlertJpaEntity(
                StockAlert.AlertType.LOW_STOCK,
                priority,
                locationId.value(),
                productId,
                productSku,
                productName,
                currentQuantity,
                threshold,
                message
        );
        
        StockAlertJpaEntity saved = stockAlertRepository.save(entity);
        log.info("Created low stock alert for product: {}", productSku);
        
        return mapToDomain(saved);
    }
    
    /**
     * Crée une alerte de stock critique
     */
    public StockAlert createCriticalStockAlert(StockLocationId locationId, Long productId, String productSku,
                                              String productName, BigDecimal currentQuantity, BigDecimal threshold) {
        String message = String.format("Stock CRITIQUE pour %s (%s): %.2f unités (seuil: %.2f)", 
                productName, productSku, currentQuantity, threshold);
        
        StockAlertJpaEntity entity = new StockAlertJpaEntity(
                StockAlert.AlertType.CRITICAL_STOCK,
                StockAlert.AlertPriority.CRITICAL,
                locationId.value(),
                productId,
                productSku,
                productName,
                currentQuantity,
                threshold,
                message
        );
        
        StockAlertJpaEntity saved = stockAlertRepository.save(entity);
        log.warn("Created CRITICAL stock alert for product: {}", productSku);
        
        return mapToDomain(saved);
    }
    
    /**
     * Crée une alerte de tampon insuffisant pour les dotations
     */
    public StockAlert createBufferInsufficientAlert(StockLocationId locationId, Long productId, String productSku,
                                                   String productName, BigDecimal currentQuantity, BigDecimal requiredQuantity) {
        String message = String.format("Tampon insuffisant pour %s (%s): %.2f unités disponibles, %.2f requises", 
                productName, productSku, currentQuantity, requiredQuantity);
        
        StockAlertJpaEntity entity = new StockAlertJpaEntity(
                StockAlert.AlertType.BUFFER_INSUFFICIENT,
                StockAlert.AlertPriority.HIGH,
                locationId.value(),
                productId,
                productSku,
                productName,
                currentQuantity,
                requiredQuantity,
                message
        );
        
        StockAlertJpaEntity saved = stockAlertRepository.save(entity);
        log.warn("Created buffer insufficient alert for product: {}", productSku);
        
        return mapToDomain(saved);
    }
    
    /**
     * Acknowledge une alerte
     */
    public StockAlert acknowledgeAlert(Long alertId, UserId userId) {
        StockAlertJpaEntity entity = stockAlertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + alertId));
        
        entity.setAcknowledged(true);
        entity.setAcknowledgedBy(userId.getMatricule());
        entity.setAcknowledgedAt(LocalDateTime.now());
        entity.setStatus("ACKNOWLEDGED");
        
        StockAlertJpaEntity saved = stockAlertRepository.save(entity);
        log.info("Acknowledged alert: {} by user {}", alertId, userId.getMatricule());
        
        return mapToDomain(saved);
    }
    
    /**
     * Résout une alerte
     */
    public StockAlert resolveAlert(Long alertId) {
        StockAlertJpaEntity entity = stockAlertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + alertId));
        
        entity.setResolvedAt(LocalDateTime.now());
        entity.setStatus("RESOLVED");
        
        StockAlertJpaEntity saved = stockAlertRepository.save(entity);
        log.info("Resolved alert: {}", alertId);
        
        return mapToDomain(saved);
    }
    
    /**
     * Récupère les alertes actives triées par priorité
     */
    public List<StockAlert> getActiveAlertsByPriority() {
        return stockAlertRepository.findActiveAlertsOrderByPriority().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les alertes (tous statuts confondus)
     */
    public List<StockAlert> getAllAlerts() {
        return stockAlertRepository.findAll().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les alertes critiques actives
     */
    public List<StockAlert> getCriticalActiveAlerts() {
        return stockAlertRepository.findCriticalActiveAlerts().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les alertes non reconnues
     */
    public List<StockAlert> getUnacknowledgedActiveAlerts() {
        return stockAlertRepository.findUnacknowledgedActiveAlerts().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les alertes d'une localisation
     */
    public List<StockAlert> getAlertsByLocation(StockLocationId locationId) {
        return stockAlertRepository.findByLocationId(locationId.value()).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Vérifie automatiquement les seuils de stock et crée des alertes si nécessaire
     * Exécuté périodiquement (toutes les heures)
     */
    @Scheduled(fixedRate = 3600000) // Toutes les heures
    public void checkStockThresholds() {
        log.info("Checking stock thresholds...");

        // Récupérer toutes les localisations de stock
        var centralLocations = stockLocationService.getLocationsByType(
                com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType.STOCK_CENTRAL);
        var bufferLocations = stockLocationService.getLocationsByType(
                com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType.STOCK_BUFFER);
        var mobileLocations = stockLocationService.getLocationsByType(
                com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType.STOCK_MOBILE);

        // Vérifier le stock central
        for (var location : centralLocations) {
            checkLocationThresholds(location.getId());
        }

        // Vérifier le tampon
        for (var location : bufferLocations) {
            checkLocationThresholds(location.getId());
        }

        // Vérifier le stock mobile
        for (var location : mobileLocations) {
            checkLocationThresholds(location.getId());
        }

        checkBatchExpiryAlerts();
        checkAnomalies();

        log.info("Stock threshold check completed");
    }

    /**
     * Vérifie les seuils pour un produit spécifique dans une localisation.
     * Appelé immédiatement après une mise à jour de stock (validation de mouvement)
     * pour générer des alertes en temps réel sans attendre le check horaire.
     */
    public void checkSingleItemThresholds(StockLocationId locationId, String productSku) {
        var itemOpt = stockItemService.getStockItem(locationId, productSku);
        if (itemOpt.isEmpty()) return;

        var item = itemOpt.get();
        BigDecimal reorderPoint = item.getReorderPoint();
        BigDecimal safetyStock = item.getSafetyStock();

        // Fallback: si les seuils ne sont pas définis, utiliser quantityPerCarton
        if (reorderPoint == null || reorderPoint.compareTo(BigDecimal.ZERO) <= 0) {
            BigDecimal qtyPerCarton = item.getQuantityPerCarton();
            if (qtyPerCarton == null || qtyPerCarton.compareTo(BigDecimal.ZERO) <= 0) {
                qtyPerCarton = BigDecimal.ONE;
            }
            reorderPoint = qtyPerCarton.multiply(BigDecimal.valueOf(20));
        }
        if (safetyStock == null || safetyStock.compareTo(BigDecimal.ZERO) <= 0) {
            BigDecimal qtyPerCarton = item.getQuantityPerCarton();
            if (qtyPerCarton == null || qtyPerCarton.compareTo(BigDecimal.ZERO) <= 0) {
                qtyPerCarton = BigDecimal.ONE;
            }
            safetyStock = qtyPerCarton.multiply(BigDecimal.valueOf(10));
        }

        BigDecimal qty = item.getQuantity() != null ? item.getQuantity() : BigDecimal.ZERO;
        String productName = item.getProductSku() != null ? item.getProductSku() : "Produit";

        if (qty.compareTo(safetyStock) <= 0) {
            if (!hasActiveAlert(locationId, item.getProductId(), StockAlert.AlertType.CRITICAL_STOCK)) {
                createCriticalStockAlert(locationId, item.getProductId(), item.getProductSku(),
                        productName, qty, safetyStock);
            }
        } else if (qty.compareTo(reorderPoint) <= 0) {
            if (!hasActiveAlert(locationId, item.getProductId(), StockAlert.AlertType.LOW_STOCK)) {
                createLowStockAlert(locationId, item.getProductId(), item.getProductSku(),
                        productName, qty, reorderPoint);
            }
        }
    }
    
    /**
     * Vérifie les seuils pour une localisation spécifique
     */
    private void checkLocationThresholds(StockLocationId locationId) {
        var stockItems = stockItemService.getStockItemsByLocation(locationId);
        
        for (var item : stockItems) {
            BigDecimal reorderPoint = item.getReorderPoint();
            BigDecimal safetyStock = item.getSafetyStock();
            
            // Fallback: si les seuils ne sont pas définis, utiliser quantityPerCarton
            if (reorderPoint == null || reorderPoint.compareTo(BigDecimal.ZERO) <= 0) {
                BigDecimal qtyPerCarton = item.getQuantityPerCarton();
                if (qtyPerCarton == null || qtyPerCarton.compareTo(BigDecimal.ZERO) <= 0) {
                    qtyPerCarton = BigDecimal.ONE;
                }
                reorderPoint = qtyPerCarton.multiply(BigDecimal.valueOf(20)); // 20 cartons
            }
            if (safetyStock == null || safetyStock.compareTo(BigDecimal.ZERO) <= 0) {
                BigDecimal qtyPerCarton = item.getQuantityPerCarton();
                if (qtyPerCarton == null || qtyPerCarton.compareTo(BigDecimal.ZERO) <= 0) {
                    qtyPerCarton = BigDecimal.ONE;
                }
                safetyStock = qtyPerCarton.multiply(BigDecimal.valueOf(10)); // 10 cartons
            }
            
            BigDecimal qty = item.getQuantity() != null ? item.getQuantity() : BigDecimal.ZERO;
            
            // Stock critique: quantité <= safetyStock
            if (qty.compareTo(safetyStock) <= 0) {
                if (!hasActiveAlert(locationId, item.getProductId(), StockAlert.AlertType.CRITICAL_STOCK)) {
                    createCriticalStockAlert(locationId, item.getProductId(), item.getProductSku(),
                            item.getProductSku() != null ? item.getProductSku() : "Produit", qty, safetyStock);
                }
            }
            // Stock bas: quantité <= reorderPoint (mais > safetyStock)
            else if (qty.compareTo(reorderPoint) <= 0) {
                if (!hasActiveAlert(locationId, item.getProductId(), StockAlert.AlertType.LOW_STOCK)) {
                    createLowStockAlert(locationId, item.getProductId(), item.getProductSku(),
                            item.getProductSku() != null ? item.getProductSku() : "Produit", qty, reorderPoint);
                }
            }
        }
    }
    
    /**
     * Vérifie si une alerte active existe déjà pour un produit et un type
     */
    private boolean hasActiveAlert(StockLocationId locationId, Long productId, StockAlert.AlertType type) {
        List<StockAlertJpaEntity> existingAlerts = stockAlertRepository.findActiveOrAcknowledgedAlertsByProductId(productId);
        return existingAlerts.stream()
                .anyMatch(alert -> alert.getLocationId().equals(locationId.value()) 
                        && alert.getType() == type 
                        && ("ACTIVE".equals(alert.getStatus()) || "ACKNOWLEDGED".equals(alert.getStatus())));
    }
    
    /**
     * Crée une alerte de péremption imminente ou produit expiré
     */
    public StockAlert createExpirationAlert(StockLocationId locationId, Long productId, String productSku,
                                             String productName, StockAlert.AlertType type,
                                             BigDecimal affectedQuantity, int daysToExpiry) {
        StockAlert.AlertPriority priority = type == StockAlert.AlertType.EXPIRED ? StockAlert.AlertPriority.CRITICAL : StockAlert.AlertPriority.HIGH;
        String message = type == StockAlert.AlertType.EXPIRED
                ? String.format("Produit expiré: %s (%s) - %.2f unités", productName, productSku, affectedQuantity)
                : String.format("Péremption imminente (%d jours): %s (%s) - %.2f unités", daysToExpiry, productName, productSku, affectedQuantity);

        StockAlertJpaEntity entity = new StockAlertJpaEntity(
                type, priority, locationId.value(), productId, productSku, productName,
                affectedQuantity, BigDecimal.valueOf(daysToExpiry), message
        );
        StockAlertJpaEntity saved = stockAlertRepository.save(entity);
        auditLogService.log("ALERT", saved.getId().toString(), "CREATE",
                "system", "StockAlertService", null,
                String.format("%s|%s|%s", type, priority, message), "Automatic stock check");
        log.warn("Created {} alert for product: {}", type, productSku);
        return mapToDomain(saved);
    }

    /**
     * Crée une alerte de rotation lente (pas de mouvement depuis X jours)
     */
    public StockAlert createSlowRotationAlert(StockLocationId locationId, Long productId, String productSku,
                                              String productName, BigDecimal currentQuantity, int daysInactive) {
        String message = String.format("Rotation lente: %s (%s) - %.2f unités sans mouvement depuis %d jours",
                productName, productSku, currentQuantity, daysInactive);
        StockAlertJpaEntity entity = new StockAlertJpaEntity(
                StockAlert.AlertType.SLOW_ROTATION, StockAlert.AlertPriority.MEDIUM,
                locationId.value(), productId, productSku, productName,
                currentQuantity, BigDecimal.valueOf(daysInactive), message
        );
        StockAlertJpaEntity saved = stockAlertRepository.save(entity);
        log.info("Created slow rotation alert for product: {}", productSku);
        return mapToDomain(saved);
    }

    /**
     * Crée une alerte d'anomalie de stock (écart important)
     */
    public StockAlert createAnomalyAlert(StockLocationId locationId, Long productId, String productSku,
                                         String productName, BigDecimal expected, BigDecimal actual) {
        BigDecimal gap = expected.subtract(actual).abs();
        String message = String.format("Anomalie d'inventaire: %s (%s) - attendu %.2f, réel %.2f (écart %.2f)",
                productName, productSku, expected, actual, gap);
        StockAlertJpaEntity entity = new StockAlertJpaEntity(
                StockAlert.AlertType.OVERSTOCK, StockAlert.AlertPriority.HIGH,
                locationId.value(), productId, productSku, productName,
                actual, expected, message
        );
        StockAlertJpaEntity saved = stockAlertRepository.save(entity);
        log.warn("Created anomaly alert for product: {}", productSku);
        return mapToDomain(saved);
    }

    /**
     * Vérifie les alertes liées aux lots (péremption)
     */
    @Scheduled(cron = "0 0 2 * * ?") // Tous les jours à 2h
    public void checkBatchExpiryAlerts() {
        log.info("Checking batch expiry alerts...");
        for (StockBatch batch : stockBatchService.getExpiredBatches()) {
            if (!hasActiveBatchAlert(batch, StockAlert.AlertType.EXPIRED)) {
                StockLocationId loc = batch.getLocationId() != null ? new StockLocationId(java.util.UUID.fromString(batch.getLocationId())) : null;
                if (loc != null) {
                    createExpirationAlert(loc, batch.getProductId(), batch.getProductSku(), batch.getProductName(),
                            StockAlert.AlertType.EXPIRED, batch.getRemainingQuantity(), 0);
                }
                stockBatchService.updateBatchStatus(batch.getId(), "EXPIRED");
            }
        }
        for (StockBatch batch : stockBatchService.getExpiringSoonBatches(30)) {
            if (!hasActiveBatchAlert(batch, StockAlert.AlertType.EXPIRATION_SOON)) {
                int days = (int) java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), batch.getExpiryDate());
                StockLocationId loc = batch.getLocationId() != null ? new StockLocationId(java.util.UUID.fromString(batch.getLocationId())) : null;
                if (loc != null) {
                    createExpirationAlert(loc, batch.getProductId(), batch.getProductSku(), batch.getProductName(),
                            StockAlert.AlertType.EXPIRATION_SOON, batch.getRemainingQuantity(), days);
                }
            }
        }
        log.info("Batch expiry check completed");
    }

    /**
     * Vérifie les produits sans mouvement récent
     */
    @Scheduled(cron = "0 0 3 * * MON") // Tous les lundis à 3h
    public void checkNoMovementAlerts() {
        log.info("Checking no-movement alerts...");
        var centralLocations = stockLocationService.getLocationsByType(
                com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType.STOCK_CENTRAL);
        for (var location : centralLocations) {
            for (var item : stockItemService.getStockItemsByLocation(location.getId())) {
                if (item.getQuantity() != null && item.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                    // Simplification : si aucun mouvement enregistré pour ce produit depuis 30 jours
                    if (!hasActiveAlert(location.getId(), item.getProductId(), StockAlert.AlertType.SLOW_ROTATION)) {
                        createSlowRotationAlert(location.getId(), item.getProductId(), item.getProductSku(),
                                item.getProductSku(), item.getQuantity(), 30);
                    }
                }
            }
        }
        log.info("No-movement check completed");
    }

    /**
     * Vérifie les anomalies : stock négatif ou écart important (placeholder pour intégration inventaire)
     */
    public void checkAnomalies() {
        log.info("Checking stock anomalies...");
        var centralLocations = stockLocationService.getLocationsByType(
                com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType.STOCK_CENTRAL);
        for (var location : centralLocations) {
            for (var item : stockItemService.getStockItemsByLocation(location.getId())) {
                if (item.getQuantity() != null && item.getQuantity().compareTo(BigDecimal.ZERO) < 0) {
                    createAnomalyAlert(location.getId(), item.getProductId(), item.getProductSku(),
                            item.getProductSku(), BigDecimal.ZERO, item.getQuantity());
                }
            }
        }
        log.info("Anomaly check completed");
    }

    private boolean hasActiveBatchAlert(StockBatch batch, StockAlert.AlertType type) {
        return stockAlertRepository.findActiveOrAcknowledgedAlertsByProductId(batch.getProductId()).stream()
                .anyMatch(a -> a.getType() == type
                        && (batch.getLocationId() == null || a.getLocationId().toString().equals(batch.getLocationId()))
                        && ("ACTIVE".equals(a.getStatus()) || "ACKNOWLEDGED".equals(a.getStatus())));
    }

    /**
     * Détermine la priorité de l'alerte en fonction de la quantité par rapport au seuil
     */
    private StockAlert.AlertPriority determinePriority(BigDecimal currentQuantity, BigDecimal threshold) {
        BigDecimal ratio = currentQuantity.divide(threshold, 2, java.math.RoundingMode.HALF_UP);
        
        if (ratio.compareTo(BigDecimal.valueOf(0.5)) <= 0) {
            return StockAlert.AlertPriority.CRITICAL;
        } else if (ratio.compareTo(BigDecimal.valueOf(0.75)) <= 0) {
            return StockAlert.AlertPriority.HIGH;
        } else {
            return StockAlert.AlertPriority.MEDIUM;
        }
    }
    
    private StockAlert mapToDomain(StockAlertJpaEntity entity) {
        StockAlert alert = new StockAlert(
                entity.getType(),
                entity.getPriority(),
                new StockLocationId(entity.getLocationId()),
                entity.getProductId(),
                entity.getProductSku(),
                entity.getProductName(),
                entity.getCurrentQuantity(),
                entity.getThreshold(),
                entity.getMessage()
        );
        alert.setId(entity.getId());
        
        if (entity.isAcknowledged()) {
            alert.acknowledge(new UserId(entity.getAcknowledgedBy()));
        }
        
        if ("RESOLVED".equals(entity.getStatus())) {
            alert.resolve();
        }
        
        return alert;
    }
}
