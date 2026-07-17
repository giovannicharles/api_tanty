package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockItem;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.jpa.StockItemJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.repository.StockItemRepository;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * StockItemService - Service pour gérer les items de stock
 */
@Service
@Transactional
public class StockItemService {
    
    private static final Logger log = LoggerFactory.getLogger(StockItemService.class);
    
    private final StockItemRepository stockItemRepository;
    
    public StockItemService(StockItemRepository stockItemRepository) {
        this.stockItemRepository = stockItemRepository;
    }
    
    /**
     * Crée ou met à jour un item de stock
     */
    @Caching(evict = {
        @CacheEvict(value = "stockItems", allEntries = true),
        @CacheEvict(value = "stockLevels", allEntries = true),
        @CacheEvict(value = "stockAlerts", allEntries = true),
        @CacheEvict(value = "dashboardStats", allEntries = true)
    })
    public StockItem createOrUpdateStockItem(StockLocationId locationId, Long productId, String productSku,
                                             String packagingType, BigDecimal quantity, BigDecimal quantityPerCarton,
                                             BigDecimal unitWeight, String volume, Integer cartonsPerAssortiment,
                                             UserId updatedBy) {
        Optional<StockItemJpaEntity> existing = stockItemRepository.findByLocationIdAndProductSku(
                locationId.value(), productSku);
        
        StockItemJpaEntity entity;
        if (existing.isPresent()) {
            entity = existing.get();
            entity.setQuantity(quantity);
            entity.setQuantityPerCarton(quantityPerCarton);
            entity.setUnitWeight(unitWeight);
            entity.setVolume(volume);
            entity.setCartonsPerAssortiment(cartonsPerAssortiment);
            entity.setLastUpdated(LocalDateTime.now());
            entity.setLastUpdatedBy(updatedBy != null ? UUID.nameUUIDFromBytes(updatedBy.getMatricule().getBytes()) : null);
            log.info("Updated stock item: {} in location {}", productSku, locationId);
        } else {
            entity = new StockItemJpaEntity(locationId.value(), productId, productSku, packagingType,
                    quantity, quantityPerCarton, unitWeight, volume, cartonsPerAssortiment);
            entity.setLastUpdatedBy(updatedBy != null ? UUID.nameUUIDFromBytes(updatedBy.getMatricule().getBytes()) : null);
            stockItemRepository.save(entity);
            log.info("Created stock item: {} in location {}", productSku, locationId);
        }
        
        return mapToDomain(entity);
    }
    
    /**
     * Récupère tous les items d'une localisation
     */
    @Cacheable(value = "stockItems", key = "#locationId.value")
    public List<StockItem> getStockItemsByLocation(StockLocationId locationId) {
        return stockItemRepository.findByLocationIdOrderByProductSku(locationId.value()).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les items de stock (tous entrepôts confondus)
     */
    @Cacheable(value = "stockItems", key = "'all'")
    public List<StockItem> getAllStockItems() {
        return stockItemRepository.findAll().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère un item spécifique
     */
    public Optional<StockItem> getStockItem(StockLocationId locationId, String productSku) {
        return stockItemRepository.findByLocationIdAndProductSku(locationId.value(), productSku)
                .map(this::mapToDomain);
    }
    
    /**
     * Ajoute une quantité à un item de stock
     */
    /**
     * Ajoute une quantité à un item de stock existant. Lève une exception si l'item
     * n'existe pas encore dans cet emplacement (usage : transferts, où la quantité
     * source doit nécessairement déjà exister).
     */
    @CacheEvict(value = {"stockItems", "stockLevels", "stockAlerts", "dashboardStats"}, allEntries = true)
    public void addQuantity(StockLocationId locationId, String productSku, BigDecimal quantityToAdd, UserId updatedBy) {
        StockItemJpaEntity entity = stockItemRepository.findByLocationIdAndProductSku(locationId.value(), productSku)
                .orElseThrow(() -> new IllegalArgumentException("Stock item not found: " + productSku + " in location " + locationId));

        BigDecimal newQuantity = entity.getQuantity().add(quantityToAdd);
        entity.setQuantity(newQuantity);
        entity.setLastUpdated(LocalDateTime.now());
        entity.setLastUpdatedBy(updatedBy != null ? UUID.nameUUIDFromBytes(updatedBy.getMatricule().getBytes()) : null);
        stockItemRepository.save(entity);

        log.info("Added {} to stock item {} in location {}", quantityToAdd, productSku, locationId);
    }

    /**
     * Ajoute une quantité à un item de stock, en le CRÉANT s'il n'existe pas encore
     * (upsert). Utilisé pour les réceptions : un produit peut être reçu en Stock
     * Central pour la toute première fois, auquel cas aucun StockItem n'existe encore
     * pour lui. L'ancienne version d'addQuantity levait alors "Stock item not found"
     * et bloquait toute validation de réception pour un nouveau produit.
     */
    @CacheEvict(value = {"stockItems", "stockLevels", "stockAlerts", "dashboardStats"}, allEntries = true)
    public void addQuantity(StockLocationId locationId, Long productId, String productSku, String packagingType,
                             BigDecimal quantityPerCarton, BigDecimal quantityToAdd, UserId updatedBy) {
        // Lookup by (locationId, productSku, packagingType) so the same product
        // can be stored under different conditionnements (carton, seau, gaine...)
        // as separate stock items.
        String pkg = (packagingType != null && !packagingType.isBlank()) ? packagingType : "CARTON";
        Optional<StockItemJpaEntity> existing = stockItemRepository
                .findByLocationIdAndProductSkuAndPackagingType(locationId.value(), productSku, pkg);
        if (existing.isPresent()) {
            StockItemJpaEntity entity = existing.get();
            entity.setQuantity(entity.getQuantity().add(quantityToAdd));
            if (quantityPerCarton != null && quantityPerCarton.compareTo(BigDecimal.ZERO) > 0) {
                entity.setQuantityPerCarton(quantityPerCarton);
            }
            entity.setLastUpdated(LocalDateTime.now());
            entity.setLastUpdatedBy(updatedBy != null ? UUID.nameUUIDFromBytes(updatedBy.getMatricule().getBytes()) : null);
            stockItemRepository.save(entity);
            log.info("Added {} to stock item {} ({}) in location {}", quantityToAdd, productSku, pkg, locationId);
        } else {
            StockItemJpaEntity entity = new StockItemJpaEntity(locationId.value(), productId, productSku, pkg,
                    quantityToAdd, quantityPerCarton, null, null, null);
            entity.setLastUpdatedBy(updatedBy != null ? UUID.nameUUIDFromBytes(updatedBy.getMatricule().getBytes()) : null);
            stockItemRepository.save(entity);
            log.info("Created stock item: {} ({}) in location {} with initial quantity {}", productSku, pkg, locationId, quantityToAdd);
        }
    }
    
    /**
     * Soustrait une quantité d'un item de stock (par packagingType)
     */
    @CacheEvict(value = {"stockItems", "stockLevels", "stockAlerts", "dashboardStats"}, allEntries = true)
    public void subtractQuantity(StockLocationId locationId, String productSku, String packagingType,
                                 BigDecimal quantityToSubtract, UserId updatedBy) {
        String pkg = (packagingType != null && !packagingType.isBlank()) ? packagingType : "CARTON";
        StockItemJpaEntity entity = stockItemRepository
                .findByLocationIdAndProductSkuAndPackagingType(locationId.value(), productSku, pkg)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Stock item not found: " + productSku + " (" + pkg + ") in location " + locationId));

        if (quantityToSubtract.compareTo(entity.getQuantity()) > 0) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + entity.getQuantity() + ", Requested: " + quantityToSubtract);
        }

        BigDecimal newQuantity = entity.getQuantity().subtract(quantityToSubtract);
        entity.setQuantity(newQuantity);
        entity.setLastUpdated(LocalDateTime.now());
        entity.setLastUpdatedBy(updatedBy != null ? UUID.nameUUIDFromBytes(updatedBy.getMatricule().getBytes()) : null);
        stockItemRepository.save(entity);

        log.info("Subtracted {} from stock item {} ({}) in location {}", quantityToSubtract, productSku, pkg, locationId);
    }

    /**
     * Soustrait une quantité d'un item de stock (sans packagingType — fallback: premier conditionnement trouvé)
     */
    @CacheEvict(value = {"stockItems", "stockLevels", "stockAlerts", "dashboardStats"}, allEntries = true)
    public void subtractQuantity(StockLocationId locationId, String productSku, BigDecimal quantityToSubtract, UserId updatedBy) {
        List<StockItemJpaEntity> items = stockItemRepository.findAllByLocationIdAndProductSku(locationId.value(), productSku);
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Stock item not found: " + productSku + " in location " + locationId);
        }
        // Subtract from the first item with sufficient quantity
        StockItemJpaEntity entity = items.stream()
                .filter(i -> i.getQuantity().compareTo(quantityToSubtract) >= 0)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Insufficient stock for " + productSku + ". Total available: " +
                        items.stream().map(StockItemJpaEntity::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add) +
                        ", Requested: " + quantityToSubtract));

        BigDecimal newQuantity = entity.getQuantity().subtract(quantityToSubtract);
        entity.setQuantity(newQuantity);
        entity.setLastUpdated(LocalDateTime.now());
        entity.setLastUpdatedBy(updatedBy != null ? UUID.nameUUIDFromBytes(updatedBy.getMatricule().getBytes()) : null);
        stockItemRepository.save(entity);

        log.info("Subtracted {} from stock item {} ({}) in location {}", quantityToSubtract, productSku, entity.getPackagingType(), locationId);
    }
    
    /**
     * Récupère les items avec stock bas
     */
    public List<StockItem> getLowStockItems(StockLocationId locationId, BigDecimal threshold) {
        return stockItemRepository.findLowStockItems(locationId.value(), threshold).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Calcule la quantité totale dans une localisation
     */
    public Optional<BigDecimal> getTotalQuantity(StockLocationId locationId) {
        return stockItemRepository.sumQuantityByLocationId(locationId.value());
    }

    /**
     * Définit les seuils (reorderPoint + safetyStock) pour un item de stock
     * Réservé au DG — le gestionnaire consulte seulement
     */
    public StockItem setThresholds(StockLocationId locationId, String productSku,
                                   BigDecimal reorderPoint, BigDecimal safetyStock) {
        StockItemJpaEntity entity = stockItemRepository.findByLocationIdAndProductSku(locationId.value(), productSku)
                .orElseThrow(() -> new IllegalArgumentException("Stock item not found: " + productSku + " in location " + locationId));
        entity.setReorderPoint(reorderPoint);
        entity.setSafetyStock(safetyStock);
        entity.setLastUpdated(LocalDateTime.now());
        stockItemRepository.save(entity);
        log.info("Thresholds set for {} in {}: reorderPoint={}, safetyStock={}", productSku, locationId, reorderPoint, safetyStock);
        return mapToDomain(entity);
    }

    /**
     * Définit les seuils pour tous les items d'une localisation (par produit)
     */
    public void setThresholdsByLocation(StockLocationId locationId, BigDecimal defaultReorderPoint, BigDecimal defaultSafetyStock) {
        List<StockItemJpaEntity> items = stockItemRepository.findByLocationIdOrderByProductSku(locationId.value());
        for (StockItemJpaEntity item : items) {
            if (item.getReorderPoint() == null) item.setReorderPoint(defaultReorderPoint);
            if (item.getSafetyStock() == null) item.setSafetyStock(defaultSafetyStock);
        }
        stockItemRepository.saveAll(items);
        log.info("Default thresholds set for {} items in location {}", items.size(), locationId);
    }

    /**
     * Récupère tous les items avec leurs seuils pour une localisation
     */
    public List<StockItem> getItemsWithThresholds(StockLocationId locationId) {
        return stockItemRepository.findByLocationIdOrderByProductSku(locationId.value()).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère tous les conditionnements d'un produit dans une localisation
     */
    public List<StockItem> getAllPackagingForProduct(StockLocationId locationId, String productSku) {
        return stockItemRepository.findAllByLocationIdAndProductSku(locationId.value(), productSku).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    private StockItem mapToDomain(StockItemJpaEntity entity) {
        StockItem item = new StockItem(
                new StockLocationId(entity.getLocationId()),
                entity.getProductId(),
                entity.getProductSku(),
                entity.getPackagingType(),
                entity.getQuantity(),
                entity.getQuantityPerCarton(),
                entity.getUnitWeight(),
                entity.getVolume(),
                entity.getCartonsPerAssortiment()
        );
        item.setId(entity.getId());
        item.setReorderPoint(entity.getReorderPoint());
        item.setSafetyStock(entity.getSafetyStock());
        // Restore the original timestamp from the database — the constructor
        // sets lastUpdated = now(), which overwrites the real value.
        item.setLastUpdated(entity.getLastUpdated());
        if (entity.getLastUpdatedBy() != null) {
            // Convert UUID back to UserId (matricule) - this is a limitation, we store UUID but can't reverse it
            // For now, we'll skip setting lastUpdatedBy
        }
        return item;
    }
}
