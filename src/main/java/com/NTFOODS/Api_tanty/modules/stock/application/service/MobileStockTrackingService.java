package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockItem;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.modules.stock.domain.dotation.entity.DotationRequest;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MobileStockTrackingService - Service pour le suivi du stock mobile des commerciaux
 * Fournit des informations détaillées sur le stock de chaque commercial
 */
@Service
@Transactional
public class MobileStockTrackingService {
    
    private final StockLocationService stockLocationService;
    private final StockItemService stockItemService;
    private final DotationService dotationService;
    private final StockMovementService stockMovementService;
    
    public MobileStockTrackingService(StockLocationService stockLocationService,
                                      StockItemService stockItemService,
                                      DotationService dotationService,
                                      StockMovementService stockMovementService) {
        this.stockLocationService = stockLocationService;
        this.stockItemService = stockItemService;
        this.dotationService = dotationService;
        this.stockMovementService = stockMovementService;
    }
    
    /**
     * Récupère le stock mobile d'un commercial
     */
    public MobileStockSummary getCommercialMobileStock(UserId commercialId, String commercialMatricule) {
        // Récupérer ou créer la localisation mobile du commercial
        StockLocationId mobileLocation = stockLocationService.getLocationByUser(new UserId(commercialMatricule))
                .map(com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockLocation::getId)
                .orElse(StockLocationId.generate()); // Retourne un ID vide si pas de location
        
        List<StockItem> stockItems = List.of();
        if (mobileLocation != null) {
            stockItems = stockItemService.getStockItemsByLocation(mobileLocation);
        }
        
        return new MobileStockSummary(
                commercialMatricule,
                mobileLocation,
                stockItems,
                calculateTotalValue(stockItems),
                calculateTotalItems(stockItems)
        );
    }
    
    /**
     * Récupère le stock mobile de tous les commerciaux
     */
    public List<MobileStockSummary> getAllCommercialsMobileStock() {
        var mobileLocations = stockLocationService.getLocationsByType(StockLocationType.STOCK_MOBILE);
        
        return mobileLocations.stream()
                .map(location -> {
                    List<StockItem> items = stockItemService.getStockItemsByLocation(location.getId());
                    return new MobileStockSummary(
                            location.getAssignedUserId() != null ? location.getAssignedUserId().getMatricule() : "Unknown",
                            location.getId(),
                            items,
                            calculateTotalValue(items),
                            calculateTotalItems(items)
                    );
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère l'historique des dotations d'un commercial
     */
    public List<DotationRequest> getCommercialDotationHistory(String commercialMatricule) {
        return dotationService.getRequestsByCommercial(commercialMatricule);
    }
    
    /**
     * Récupère les détails de stock d'un produit spécifique pour un commercial
     */
    public Optional<ProductStockDetail> getCommercialProductStockDetail(String commercialMatricule, String productSku) {
        StockLocationId mobileLocation = stockLocationService.getLocationByUser(new UserId(commercialMatricule))
                .map(com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockLocation::getId)
                .orElse(null);
        
        if (mobileLocation == null) {
            return Optional.empty();
        }
        
        return stockItemService.getStockItem(mobileLocation, productSku)
                .map(item -> new ProductStockDetail(
                        item.getProductSku(),
                        "Produit", // Nom du produit (à récupérer depuis le service produit)
                        item.getPackagingType(),
                        item.getQuantity(),
                        item.getQuantityPerCarton(),
                        item.calculateCartons(),
                        item.getUnitWeight(),
                        item.getVolume(),
                        item.getCartonsPerAssortiment()
                ));
    }
    
    /**
     * Calcule la rotation du stock d'un commercial
     */
    public StockRotationMetrics calculateCommercialStockRotation(String commercialMatricule, int days) {
        StockLocationId mobileLocation = stockLocationService.getLocationByUser(new UserId(commercialMatricule))
                .map(com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockLocation::getId)
                .orElse(null);
        
        if (mobileLocation == null) {
            return new StockRotationMetrics(commercialMatricule, BigDecimal.ZERO, BigDecimal.ZERO, 0);
        }
        
        // Récupérer les mouvements de stock sur la période
        var movements = stockMovementService.getMovementsByLocation(mobileLocation);
        
        // Calculer les métriques
        BigDecimal totalIn = movements.stream()
                .filter(m -> m.getType() == com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockMovementType.TRANSFER_BUFFER_TO_MOBILE)
                .map(com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockMovement::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalOut = movements.stream()
                .filter(m -> m.getType() == com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockMovementType.SALE)
                .map(com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockMovement::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int movementCount = movements.size();
        
        return new StockRotationMetrics(commercialMatricule, totalIn, totalOut, movementCount);
    }
    
    /**
     * Identifie les commerciaux avec stock lent (rotation faible)
     */
    public List<SlowStockCommercial> identifySlowStockCommercials(int daysThreshold, BigDecimal salesThreshold) {
        var mobileLocations = stockLocationService.getLocationsByType(StockLocationType.STOCK_MOBILE);
        
        return mobileLocations.stream()
                .filter(loc -> loc.getAssignedUserId() != null)
                .map(location -> {
                    String matricule = location.getAssignedUserId().getMatricule();
                    StockRotationMetrics metrics = calculateCommercialStockRotation(matricule, daysThreshold);
                    
                    if (metrics.getTotalOut().compareTo(salesThreshold) < 0) {
                        List<StockItem> items = stockItemService.getStockItemsByLocation(location.getId());
                        return new SlowStockCommercial(
                                matricule,
                                location.getName(),
                                metrics.getTotalOut(),
                                calculateTotalItems(items),
                                items.size()
                        );
                    }
                    return null;
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    private BigDecimal calculateTotalValue(List<StockItem> items) {
        // Dans un système réel, on multiplierait par le prix unitaire
        // Pour simplifier, on retourne la quantité totale
        return items.stream()
                .map(StockItem::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private int calculateTotalItems(List<StockItem> items) {
        return items.stream()
                .mapToInt(item -> item.getQuantity().intValue())
                .sum();
    }
    
    /**
     * MobileStockSummary - Résumé du stock mobile d'un commercial
     */
    public static class MobileStockSummary {
        private final String commercialMatricule;
        private final StockLocationId locationId;
        private final List<StockItem> stockItems;
        private final BigDecimal totalValue;
        private final int totalItems;
        
        public MobileStockSummary(String commercialMatricule, StockLocationId locationId,
                                 List<StockItem> stockItems, BigDecimal totalValue, int totalItems) {
            this.commercialMatricule = commercialMatricule;
            this.locationId = locationId;
            this.stockItems = stockItems;
            this.totalValue = totalValue;
            this.totalItems = totalItems;
        }
        
        public String getCommercialMatricule() { return commercialMatricule; }
        public StockLocationId getLocationId() { return locationId; }
        public List<StockItem> getStockItems() { return stockItems; }
        public BigDecimal getTotalValue() { return totalValue; }
        public int getTotalItems() { return totalItems; }
    }
    
    /**
     * ProductStockDetail - Détails de stock d'un produit
     */
    public static class ProductStockDetail {
        private final String productSku;
        private final String productName;
        private final String packagingType;
        private final BigDecimal quantity;
        private final BigDecimal quantityPerCarton;
        private final BigDecimal cartons;
        private final BigDecimal unitWeight;
        private final String volume;
        private final Integer cartonsPerAssortiment;
        
        public ProductStockDetail(String productSku, String productName, String packagingType,
                                 BigDecimal quantity, BigDecimal quantityPerCarton, BigDecimal cartons,
                                 BigDecimal unitWeight, String volume, Integer cartonsPerAssortiment) {
            this.productSku = productSku;
            this.productName = productName;
            this.packagingType = packagingType;
            this.quantity = quantity;
            this.quantityPerCarton = quantityPerCarton;
            this.cartons = cartons;
            this.unitWeight = unitWeight;
            this.volume = volume;
            this.cartonsPerAssortiment = cartonsPerAssortiment;
        }
        
        public String getProductSku() { return productSku; }
        public String getProductName() { return productName; }
        public String getPackagingType() { return packagingType; }
        public BigDecimal getQuantity() { return quantity; }
        public BigDecimal getQuantityPerCarton() { return quantityPerCarton; }
        public BigDecimal getCartons() { return cartons; }
        public BigDecimal getUnitWeight() { return unitWeight; }
        public String getVolume() { return volume; }
        public Integer getCartonsPerAssortiment() { return cartonsPerAssortiment; }
    }
    
    /**
     * StockRotationMetrics - Métriques de rotation de stock
     */
    public static class StockRotationMetrics {
        private final String commercialMatricule;
        private final BigDecimal totalIn;
        private final BigDecimal totalOut;
        private final int movementCount;
        
        public StockRotationMetrics(String commercialMatricule, BigDecimal totalIn, 
                                   BigDecimal totalOut, int movementCount) {
            this.commercialMatricule = commercialMatricule;
            this.totalIn = totalIn;
            this.totalOut = totalOut;
            this.movementCount = movementCount;
        }
        
        public String getCommercialMatricule() { return commercialMatricule; }
        public BigDecimal getTotalIn() { return totalIn; }
        public BigDecimal getTotalOut() { return totalOut; }
        public int getMovementCount() { return movementCount; }
        
        public BigDecimal getNetMovement() {
            return totalOut.subtract(totalIn);
        }
    }
    
    /**
     * SlowStockCommercial - Commercial avec stock lent
     */
    public static class SlowStockCommercial {
        private final String matricule;
        private final String locationName;
        private final BigDecimal totalSales;
        private final int totalStock;
        private final int productCount;
        
        public SlowStockCommercial(String matricule, String locationName, BigDecimal totalSales,
                                  int totalStock, int productCount) {
            this.matricule = matricule;
            this.locationName = locationName;
            this.totalSales = totalSales;
            this.totalStock = totalStock;
            this.productCount = productCount;
        }
        
        public String getMatricule() { return matricule; }
        public String getLocationName() { return locationName; }
        public BigDecimal getTotalSales() { return totalSales; }
        public int getTotalStock() { return totalStock; }
        public int getProductCount() { return productCount; }
    }
}
