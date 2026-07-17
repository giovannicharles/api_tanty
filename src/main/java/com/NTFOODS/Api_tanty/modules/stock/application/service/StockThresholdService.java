package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.modules.stock.domain.seuil.entity.StockThreshold;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.seuil.jpa.StockThresholdJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.seuil.repository.StockThresholdRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * StockThresholdService - Service pour gérer les seuils de stock
 * Définit et gère les seuils minimum, maximum et de réapprovisionnement
 */
@Service
@Transactional
public class StockThresholdService {
    
    private static final Logger log = LoggerFactory.getLogger(StockThresholdService.class);
    
    private final StockThresholdRepository stockThresholdRepository;
    private final StockItemService stockItemService;
    private final StockAlertService stockAlertService;
    
    public StockThresholdService(StockThresholdRepository stockThresholdRepository,
                                StockItemService stockItemService,
                                StockAlertService stockAlertService) {
        this.stockThresholdRepository = stockThresholdRepository;
        this.stockItemService = stockItemService;
        this.stockAlertService = stockAlertService;
    }
    
    /**
     * Crée un nouveau seuil de stock
     */
    public StockThreshold createThreshold(StockLocationId locationId, Long productId, String productSku,
                                         BigDecimal minimumThreshold, BigDecimal maximumThreshold,
                                         BigDecimal reorderThreshold, BigDecimal reorderQuantity, String notes) {
        StockThresholdJpaEntity entity = new StockThresholdJpaEntity(
                locationId.value(),
                productId,
                productSku,
                minimumThreshold,
                maximumThreshold,
                reorderThreshold,
                reorderQuantity,
                notes
        );
        
        StockThresholdJpaEntity saved = stockThresholdRepository.save(entity);
        log.info("Created stock threshold for product: {} in location: {}", productSku, locationId);

        // SYNC: Also update StockItem.reorderPoint and safetyStock so that
        // StockAlertService.checkStockThresholds() uses the DG-defined values.
        try {
            stockItemService.setThresholds(locationId, productSku, reorderThreshold, minimumThreshold);
        } catch (IllegalArgumentException e) {
            log.warn("Stock item not found for threshold sync: {} in {} — thresholds will apply once item is created", productSku, locationId);
        }

        return mapToDomain(saved);
    }
    
    /**
     * Met à jour un seuil de stock
     */
    public StockThreshold updateThreshold(Long thresholdId, BigDecimal minimumThreshold, 
                                         BigDecimal maximumThreshold, BigDecimal reorderThreshold,
                                         BigDecimal reorderQuantity, String notes) {
        StockThresholdJpaEntity entity = stockThresholdRepository.findById(thresholdId)
                .orElseThrow(() -> new IllegalArgumentException("Threshold not found: " + thresholdId));
        
        entity.setMinimumThreshold(minimumThreshold);
        entity.setMaximumThreshold(maximumThreshold);
        entity.setReorderThreshold(reorderThreshold);
        entity.setReorderQuantity(reorderQuantity);
        entity.setNotes(notes);
        entity.setUpdatedAt(java.time.LocalDateTime.now());
        
        StockThresholdJpaEntity saved = stockThresholdRepository.save(entity);
        log.info("Updated stock threshold: {}", thresholdId);

        // SYNC: Also update StockItem.reorderPoint and safetyStock so that
        // StockAlertService.checkStockThresholds() uses the DG-defined values.
        try {
            StockLocationId locationId = new StockLocationId(entity.getLocationId());
            stockItemService.setThresholds(locationId, entity.getProductSku(), reorderThreshold, minimumThreshold);
        } catch (IllegalArgumentException e) {
            log.warn("Stock item not found for threshold sync: {} — thresholds will apply once item is created", entity.getProductSku());
        }

        return mapToDomain(saved);
    }
    
    /**
     * Récupère un seuil par localisation et produit
     */
    public Optional<StockThreshold> getThreshold(StockLocationId locationId, Long productId) {
        return stockThresholdRepository.findByLocationIdAndProductId(locationId.value(), productId)
                .map(this::mapToDomain);
    }
    
    /**
     * Récupère un seuil par localisation et SKU
     */
    public Optional<StockThreshold> getThresholdBySku(StockLocationId locationId, String productSku) {
        return stockThresholdRepository.findByLocationIdAndProductSku(locationId.value(), productSku)
                .map(this::mapToDomain);
    }
    
    /**
     * Récupère tous les seuils d'une localisation
     */
    public List<StockThreshold> getThresholdsByLocation(StockLocationId locationId) {
        return stockThresholdRepository.findByLocationIdOrderByProductSku(locationId.value()).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère tous les seuils d'un produit
     */
    public List<StockThreshold> getThresholdsByProduct(Long productId) {
        return stockThresholdRepository.findByProductIdOrderByLocationId(productId).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    public List<StockThreshold> getAllThresholds() {
        return stockThresholdRepository.findAll().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Supprime un seuil
     */
    public void deleteThreshold(Long thresholdId) {
        StockThresholdJpaEntity entity = stockThresholdRepository.findById(thresholdId)
                .orElseThrow(() -> new IllegalArgumentException("Threshold not found: " + thresholdId));
        // SYNC: Clear thresholds on StockItem when DG threshold is deleted
        try {
            StockLocationId locationId = new StockLocationId(entity.getLocationId());
            stockItemService.setThresholds(locationId, entity.getProductSku(), BigDecimal.ZERO, BigDecimal.ZERO);
        } catch (IllegalArgumentException e) {
            // Item may not exist — nothing to clear
        }
        stockThresholdRepository.deleteById(thresholdId);
        log.info("Deleted stock threshold: {}", thresholdId);
    }
    
    /**
     * Vérifie tous les seuils et crée des alertes si nécessaire
     */
    public void checkAllThresholds() {
        log.info("Checking all stock thresholds...");
        
        List<StockThresholdJpaEntity> allThresholds = stockThresholdRepository.findAll();
        
        for (StockThresholdJpaEntity thresholdEntity : allThresholds) {
            StockThreshold threshold = mapToDomain(thresholdEntity);
            StockLocationId locationId = new StockLocationId(thresholdEntity.getLocationId());
            
            // Récupérer la quantité actuelle
            Optional<com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockItem> stockItem =
                    stockItemService.getStockItem(locationId, threshold.getProductSku());
            
            if (stockItem.isPresent()) {
                BigDecimal currentQuantity = stockItem.get().getQuantity();
                
                // Vérifier si en dessous du minimum
                if (threshold.isBelowMinimum(currentQuantity)) {
                    stockAlertService.createLowStockAlert(
                            locationId,
                            threshold.getProductId(),
                            threshold.getProductSku(),
                            "Produit",
                            currentQuantity,
                            threshold.getMinimumThreshold()
                    );
                }
                
                // Vérifier si au-dessus du maximum
                if (threshold.isAboveMaximum(currentQuantity)) {
                    // Créer une alerte de surstock (à implémenter dans StockAlertService)
                    log.warn("Overstock detected for product: {} - Quantity: {}, Max: {}", 
                            threshold.getProductSku(), currentQuantity, threshold.getMaximumThreshold());
                }
                
                // Vérifier si réapprovisionnement nécessaire
                if (threshold.needsReorder(currentQuantity)) {
                    log.info("Reorder needed for product: {} - Current: {}, Threshold: {}", 
                            threshold.getProductSku(), currentQuantity, threshold.getReorderThreshold());
                    // Ici, on pourrait créer une suggestion de commande automatique
                }
            }
        }
        
        log.info("Stock threshold check completed");
    }
    
    /**
     * Calcule la quantité de réapprovisionnement recommandée
     */
    public BigDecimal calculateRecommendedReorderQuantity(StockLocationId locationId, String productSku) {
        Optional<StockThreshold> thresholdOpt = getThresholdBySku(locationId, productSku);
        
        if (thresholdOpt.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        StockThreshold threshold = thresholdOpt.get();
        Optional<com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockItem> stockItem =
                stockItemService.getStockItem(locationId, productSku);
        
        if (stockItem.isEmpty()) {
            return threshold.getReorderQuantity();
        }
        
        return threshold.calculateReorderQuantity(stockItem.get().getQuantity());
    }
    
    private StockThreshold mapToDomain(StockThresholdJpaEntity entity) {
        StockThreshold threshold = new StockThreshold(
                new StockLocationId(entity.getLocationId()),
                entity.getProductId(),
                entity.getProductSku(),
                entity.getMinimumThreshold(),
                entity.getMaximumThreshold(),
                entity.getReorderThreshold(),
                entity.getReorderQuantity(),
                entity.getNotes()
        );
        threshold.setId(entity.getId());
        return threshold;
    }
}
