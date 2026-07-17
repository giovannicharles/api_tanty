package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockLocation;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.jpa.StockLocationJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.repository.StockLocationRepository;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * StockLocationService - Service pour gérer les localisations de stock et magasins
 */
@Service
@Transactional
public class StockLocationService {
    
    private static final Logger log = LoggerFactory.getLogger(StockLocationService.class);
    
    private final StockLocationRepository stockLocationRepository;
    private final StockItemService stockItemService;
    
    public StockLocationService(StockLocationRepository stockLocationRepository, StockItemService stockItemService) {
        this.stockLocationRepository = stockLocationRepository;
        this.stockItemService = stockItemService;
    }
    
    /**
     * Crée une nouvelle localisation de stock
     */
    public StockLocationId createLocation(StockLocationType type, String name, String description) {
        StockLocationId locationId = StockLocationId.generate();
        StockLocationJpaEntity entity = new StockLocationJpaEntity(locationId.value(), type, name, description);
        stockLocationRepository.save(entity);
        log.info("Created stock location: {} - {}", type, name);
        return locationId;
    }
    
    /**
     * Récupère une localisation par son ID
     */
    public Optional<StockLocation> getLocationById(StockLocationId locationId) {
        return stockLocationRepository.findByLocationId(locationId.value())
                .map(this::mapToDomain);
    }
    
    /**
     * Récupère toutes les localisations d'un type donné
     */
    public List<StockLocation> getLocationsByType(StockLocationType type) {
        return stockLocationRepository.findByType(type).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère la localisation assignée à un utilisateur (stock mobile)
     */
    public Optional<StockLocation> getLocationByUser(UserId userId) {
        return stockLocationRepository.findByAssignedUserId(userId.getMatricule())
                .map(this::mapToDomain);
    }
    
    /**
     * Assigne une localisation à un utilisateur
     */
    public void assignLocationToUser(StockLocationId locationId, UserId userId) {
        StockLocationJpaEntity entity = stockLocationRepository.findByLocationId(locationId.value())
                .orElseThrow(() -> new IllegalArgumentException("Location not found: " + locationId));
        entity.setAssignedUserId(userId.getMatricule());
        entity.setUpdatedAt(java.time.LocalDateTime.now());
        stockLocationRepository.save(entity);
        log.info("Assigned location {} to user {}", locationId, userId);
    }

    // ═══ MAGASIN MANAGEMENT ═══════════════════════════════════

    /**
     * Crée un magasin avec informations détaillées
     */
    public StockLocationId createMagasin(String name, String description, String managerId,
                                          String address, String phone, String email) {
        StockLocationId locationId = StockLocationId.generate();
        StockLocationJpaEntity entity = new StockLocationJpaEntity(locationId.value(), StockLocationType.MAGASIN, name, description);
        entity.setManagerId(managerId);
        entity.setAddress(address);
        entity.setPhone(phone);
        entity.setEmail(email);
        entity.setActive(true);
        stockLocationRepository.save(entity);
        log.info("Created magasin: {} (manager: {})", name, managerId);
        return locationId;
    }

    /**
     * Met à jour un magasin
     */
    public void updateMagasin(StockLocationId locationId, String name, String description,
                               String managerId, String address, String phone, String email, Boolean active) {
        StockLocationJpaEntity entity = stockLocationRepository.findByLocationId(locationId.value())
                .orElseThrow(() -> new IllegalArgumentException("Magasin introuvable: " + locationId));
        if (!entity.getType().equals(StockLocationType.MAGASIN)) {
            throw new IllegalArgumentException("La localisation n'est pas un magasin");
        }
        if (name != null) entity.setName(name);
        if (description != null) entity.setDescription(description);
        if (managerId != null) entity.setManagerId(managerId);
        if (address != null) entity.setAddress(address);
        if (phone != null) entity.setPhone(phone);
        if (email != null) entity.setEmail(email);
        if (active != null) entity.setActive(active);
        entity.setUpdatedAt(java.time.LocalDateTime.now());
        stockLocationRepository.save(entity);
        log.info("Updated magasin: {}", locationId);
    }

    /**
     * Récupère tous les magasins
     */
    public List<StockLocation> getAllMagasins() {
        return stockLocationRepository.findByType(StockLocationType.MAGASIN).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les magasins actifs uniquement
     */
    public List<StockLocation> getActiveMagasins() {
        return stockLocationRepository.findByTypeAndActiveTrue(StockLocationType.MAGASIN).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les magasins gérés par un gestionnaire
     */
    public List<StockLocation> getMagasinsByManager(String managerMatricule) {
        return stockLocationRepository.findByManagerId(managerMatricule).stream()
                .filter(e -> e.getType().equals(StockLocationType.MAGASIN))
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    /**
     * Compte le nombre d'articles en stock dans un magasin
     */
    public int getMagasinItemCount(StockLocationId locationId) {
        return stockItemService.getStockItemsByLocation(locationId).size();
    }

    /**
     * Supprime (désactive) un magasin
     */
    public void deactivateMagasin(StockLocationId locationId) {
        StockLocationJpaEntity entity = stockLocationRepository.findByLocationId(locationId.value())
                .orElseThrow(() -> new IllegalArgumentException("Magasin introuvable: " + locationId));
        entity.setActive(false);
        entity.setUpdatedAt(java.time.LocalDateTime.now());
        stockLocationRepository.save(entity);
        log.info("Deactivated magasin: {}", locationId);
    }
    
    /**
     * Initialise les localisations de stock par défaut
     */
    public void initializeDefaultLocations() {
        // Stock Central
        if (stockLocationRepository.findByType(StockLocationType.STOCK_CENTRAL).isEmpty()) {
            createLocation(StockLocationType.STOCK_CENTRAL, "Stock Central", "Stock principal de l'entreprise");
        }
        
        // Tampon (Buffer)
        if (stockLocationRepository.findByType(StockLocationType.STOCK_BUFFER).isEmpty()) {
            createLocation(StockLocationType.STOCK_BUFFER, "Tampon", "Stock intermédiaire pour les dotations");
        }
        
        log.info("Default stock locations initialized");
    }
    
    private StockLocation mapToDomain(StockLocationJpaEntity entity) {
        StockLocation location = new StockLocation(
                new StockLocationId(entity.getLocationId()),
                entity.getType(),
                entity.getName(),
                entity.getDescription()
        );
        if (entity.getAssignedUserId() != null) {
            location.setAssignedUserId(new UserId(entity.getAssignedUserId()));
        }
        location.setManagerId(entity.getManagerId());
        location.setAddress(entity.getAddress());
        location.setPhone(entity.getPhone());
        location.setEmail(entity.getEmail());
        location.setActive(entity.getActive());
        location.setCreatedAt(entity.getCreatedAt());
        location.setUpdatedAt(entity.getUpdatedAt());
        return location;
    }
}
