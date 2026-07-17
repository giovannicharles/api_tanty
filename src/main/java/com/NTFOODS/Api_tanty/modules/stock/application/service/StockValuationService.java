package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.application.dto.BufferValuationResponse;
import com.NTFOODS.Api_tanty.modules.stock.application.dto.ProductCatalogResponse;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockItem;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductPriceJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductPriceJpaRepository;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.jpa.StockItemJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.repository.StockItemRepository;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.repository.StockLocationRepository;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * StockValuationService - Service pour intégrer le module Finance
 * Calcule la valorisation du stock (valeur totale des stocks)
 */
@Service
@Transactional
public class StockValuationService {
    
    private static final Logger log = LoggerFactory.getLogger(StockValuationService.class);
    
    private final StockItemService stockItemService;
    private final StockLocationService stockLocationService;
    private final ProductPriceJpaRepository productPriceRepository;
    private final StockItemRepository stockItemRepository;
    private final StockLocationRepository stockLocationRepository;
    private final ProductCatalogService productCatalogService;
    
    public StockValuationService(StockItemService stockItemService,
                                StockLocationService stockLocationService,
                                ProductPriceJpaRepository productPriceRepository,
                                StockItemRepository stockItemRepository,
                                StockLocationRepository stockLocationRepository,
                                ProductCatalogService productCatalogService) {
        this.stockItemService = stockItemService;
        this.stockLocationService = stockLocationService;
        this.productPriceRepository = productPriceRepository;
        this.stockItemRepository = stockItemRepository;
        this.stockLocationRepository = stockLocationRepository;
        this.productCatalogService = productCatalogService;
    }
    
    /**
     * Calcule la valeur totale du stock d'une localisation
     * Note: Dans un système réel, on récupérerait le prix unitaire depuis le module Finance
     */
    public BigDecimal calculateStockValue(StockLocationId locationId, Map<String, BigDecimal> unitPrices) {
        List<StockItem> stockItems = stockItemService.getStockItemsByLocation(locationId);
        
        BigDecimal totalValue = stockItems.stream()
                .map(item -> {
                    BigDecimal unitPrice = unitPrices.getOrDefault(item.getProductSku(), BigDecimal.ZERO);
                    return item.getQuantity().multiply(unitPrice);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        log.info("Calculated stock value for location {}: {}", locationId, totalValue);
        return totalValue;
    }
    
    /**
     * Calcule la valeur totale du stock central
     */
    public BigDecimal calculateCentralStockValue(Map<String, BigDecimal> unitPrices) {
        var centralLocations = stockLocationService.getLocationsByType(
                com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType.STOCK_CENTRAL);
        
        if (centralLocations.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return calculateStockValue(centralLocations.get(0).getId(), unitPrices);
    }
    
    /**
     * Calcule la valeur totale du stock tampon
     */
    public BigDecimal calculateBufferStockValue(Map<String, BigDecimal> unitPrices) {
        var bufferLocations = stockLocationService.getLocationsByType(
                com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType.STOCK_BUFFER);
        
        if (bufferLocations.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return calculateStockValue(bufferLocations.get(0).getId(), unitPrices);
    }

    /**
     * Calcule automatiquement la valorisation du stock tampon en récupérant les prix
     * unitaires depuis la base (ProductPriceJpaRepository). Utilise le prix DETAIL par
     * défaut, puis RETAIL, puis WHOLESALE, puis DISTRIBUTOR comme fallback.
     */
    public BufferValuationResponse calculateBufferValuationAuto() {
        var bufferLocs = stockLocationRepository.findByType(StockLocationType.STOCK_BUFFER);
        if (bufferLocs.isEmpty()) {
            return new BufferValuationResponse(BigDecimal.ZERO, 0, "XAF", new ArrayList<>());
        }

        UUID bufferLocationId = bufferLocs.get(0).getLocationId();
        List<StockItemJpaEntity> items = stockItemRepository.findByLocationId(bufferLocationId);

        List<BufferValuationResponse.BufferValuationItem> valuationItems = new ArrayList<>();
        BigDecimal totalValue = BigDecimal.ZERO;

        for (StockItemJpaEntity item : items) {
            if (item.getProductId() == null) continue;

            BigDecimal unitPrice = getUnitPrice(item.getProductId());
            BigDecimal itemValue = item.getQuantity().multiply(unitPrice);
            totalValue = totalValue.add(itemValue);

            ProductCatalogResponse product = productCatalogService.getById(item.getProductId()).orElse(null);
            String productName = product != null ? product.getDesignation() : item.getProductSku();

            valuationItems.add(new BufferValuationResponse.BufferValuationItem(
                    item.getProductSku(),
                    productName,
                    item.getQuantity(),
                    unitPrice,
                    itemValue,
                    "DETAIL",
                    "XAF"
            ));
        }

        log.info("Buffer valuation calculated: {} items, total value = {} XAF", valuationItems.size(), totalValue);
        return new BufferValuationResponse(totalValue, valuationItems.size(), "XAF", valuationItems);
    }

    /**
     * Calcule automatiquement la valorisation d'une localisation par type (CENTRAL, BUFFER, MOBILE).
     * Récupère les prix unitaires depuis la base de données.
     */
    public BufferValuationResponse calculateLocationValuationAuto(StockLocationType locationType) {
        var locations = stockLocationRepository.findByType(locationType);
        if (locations.isEmpty()) {
            return new BufferValuationResponse(BigDecimal.ZERO, 0, "XAF", new ArrayList<>());
        }

        UUID locationId = locations.get(0).getLocationId();
        List<StockItemJpaEntity> items = stockItemRepository.findByLocationId(locationId);

        List<BufferValuationResponse.BufferValuationItem> valuationItems = new ArrayList<>();
        BigDecimal totalValue = BigDecimal.ZERO;

        for (StockItemJpaEntity item : items) {
            if (item.getProductId() == null) continue;

            BigDecimal unitPrice = getUnitPrice(item.getProductId());
            BigDecimal itemValue = item.getQuantity().multiply(unitPrice);
            totalValue = totalValue.add(itemValue);

            ProductCatalogResponse product = productCatalogService.getById(item.getProductId()).orElse(null);
            String productName = product != null ? product.getDesignation() : item.getProductSku();

            valuationItems.add(new BufferValuationResponse.BufferValuationItem(
                    item.getProductSku(),
                    productName,
                    item.getQuantity(),
                    unitPrice,
                    itemValue,
                    "DETAIL",
                    "XAF"
            ));
        }

        log.info("{} valuation calculated: {} items, total value = {} XAF", locationType, valuationItems.size(), totalValue);
        return new BufferValuationResponse(totalValue, valuationItems.size(), "XAF", valuationItems);
    }

    /**
     * Calcule automatiquement la valorisation totale (central + tampon + mobile)
     * en utilisant les prix de la base de données, sans nécessiter de saisie manuelle.
     */
    public StockValuationSummary calculateTotalValuationAuto() {
        BigDecimal centralValue = calculateLocationValuationAuto(StockLocationType.STOCK_CENTRAL).getTotalValue();
        BigDecimal bufferValue = calculateLocationValuationAuto(StockLocationType.STOCK_BUFFER).getTotalValue();
        BigDecimal mobileValue = BigDecimal.ZERO;
        var mobileLocs = stockLocationRepository.findByType(StockLocationType.STOCK_MOBILE);
        for (var loc : mobileLocs) {
            List<StockItemJpaEntity> items = stockItemRepository.findByLocationId(loc.getLocationId());
            for (StockItemJpaEntity item : items) {
                if (item.getProductId() == null) continue;
                BigDecimal unitPrice = getUnitPrice(item.getProductId());
                mobileValue = mobileValue.add(item.getQuantity().multiply(unitPrice));
            }
        }
        BigDecimal totalValue = centralValue.add(bufferValue).add(mobileValue);
        return new StockValuationSummary(centralValue, bufferValue, mobileValue, totalValue);
    }

    /**
     * Récupère le prix unitaire d'un produit en essayant DETAIL > RETAIL > WHOLESALE > DISTRIBUTOR.
     */
    private BigDecimal getUnitPrice(Long productId) {
        String[] priceTypes = {"DETAIL", "RETAIL", "WHOLESALE", "DISTRIBUTOR"};
        for (String type : priceTypes) {
            Optional<ProductPriceJpaEntity> price = productPriceRepository
                    .findByProductIdAndPriceTypeAndActiveTrue(productId, type);
            if (price.isPresent()) {
                return price.get().getPrice();
            }
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Calcule la valeur totale du stock mobile
     */
    public BigDecimal calculateMobileStockValue(Map<String, BigDecimal> unitPrices) {
        var mobileLocations = stockLocationService.getLocationsByType(
                com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType.STOCK_MOBILE);
        
        BigDecimal totalValue = mobileLocations.stream()
                .map(location -> calculateStockValue(location.getId(), unitPrices))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return totalValue;
    }
    
    /**
     * Calcule la valeur totale de tout le stock
     */
    public StockValuationSummary calculateTotalStockValue(Map<String, BigDecimal> unitPrices) {
        BigDecimal centralValue = calculateCentralStockValue(unitPrices);
        BigDecimal bufferValue = calculateBufferStockValue(unitPrices);
        BigDecimal mobileValue = calculateMobileStockValue(unitPrices);
        
        BigDecimal totalValue = centralValue.add(bufferValue).add(mobileValue);
        
        return new StockValuationSummary(
                centralValue,
                bufferValue,
                mobileValue,
                totalValue
        );
    }
    
    /**
     * Calcule la valeur par catégorie de produit
     */
    public Map<String, BigDecimal> calculateValueByCategory(StockLocationId locationId, 
                                                             Map<String, BigDecimal> unitPrices,
                                                             Map<String, String> productCategories) {
        List<StockItem> stockItems = stockItemService.getStockItemsByLocation(locationId);
        
        return stockItems.stream()
                .collect(Collectors.groupingBy(
                        item -> productCategories.getOrDefault(item.getProductSku(), "UNCATEGORIZED"),
                        Collectors.mapping(
                                item -> {
                                    BigDecimal unitPrice = unitPrices.getOrDefault(item.getProductSku(), BigDecimal.ZERO);
                                    return item.getQuantity().multiply(unitPrice);
                                },
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));
    }
    
    /**
     * Calcule le coût de stockage par unité de temps
     * Note: Ceci est une estimation simplifiée
     */
    public BigDecimal calculateStorageCost(StockLocationId locationId, 
                                         Map<String, BigDecimal> unitPrices,
                                         BigDecimal storageCostRate) {
        BigDecimal stockValue = calculateStockValue(locationId, unitPrices);
        return stockValue.multiply(storageCostRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
    
    /**
     * StockValuationSummary - Résumé de la valorisation du stock
     */
    public static class StockValuationSummary {
        private final BigDecimal centralValue;
        private final BigDecimal bufferValue;
        private final BigDecimal mobileValue;
        private final BigDecimal totalValue;
        
        public StockValuationSummary(BigDecimal centralValue, BigDecimal bufferValue, 
                                    BigDecimal mobileValue, BigDecimal totalValue) {
            this.centralValue = centralValue;
            this.bufferValue = bufferValue;
            this.mobileValue = mobileValue;
            this.totalValue = totalValue;
        }
        
        public BigDecimal getCentralValue() {
            return centralValue;
        }
        
        public BigDecimal getBufferValue() {
            return bufferValue;
        }
        
        public BigDecimal getMobileValue() {
            return mobileValue;
        }
        
        public BigDecimal getTotalValue() {
            return totalValue;
        }
    }
}
