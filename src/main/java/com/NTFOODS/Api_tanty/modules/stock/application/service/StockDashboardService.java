package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * StockDashboardService - Service pour fournir les KPIs en temps réel pour le dashboard
 */
@Service
@Transactional
public class StockDashboardService {
    
    private final StockLocationService stockLocationService;
    private final StockItemService stockItemService;
    private final StockMovementService stockMovementService;
    private final StockAlertService stockAlertService;
    private final DotationService dotationService;
    private final MobileStockTrackingService mobileStockTrackingService;
    
    public StockDashboardService(StockLocationService stockLocationService,
                                StockItemService stockItemService,
                                StockMovementService stockMovementService,
                                StockAlertService stockAlertService,
                                DotationService dotationService,
                                MobileStockTrackingService mobileStockTrackingService) {
        this.stockLocationService = stockLocationService;
        this.stockItemService = stockItemService;
        this.stockMovementService = stockMovementService;
        this.stockAlertService = stockAlertService;
        this.dotationService = dotationService;
        this.mobileStockTrackingService = mobileStockTrackingService;
    }
    
    /**
     * Récupère les KPIs globaux du stock
     */
    public Map<String, Object> getGlobalStockKPIs() {
        Map<String, Object> kpis = new HashMap<>();
        
        // Stock Central
        StockLocationId centralLocation = stockLocationService.getLocationsByType(StockLocationType.STOCK_CENTRAL)
                .stream()
                .findFirst()
                .map(com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockLocation::getId)
                .orElse(null);
        
        if (centralLocation != null) {
            List<com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockItem> centralItems = 
                    stockItemService.getStockItemsByLocation(centralLocation);
            kpis.put("centralTotalProducts", centralItems.size());
            kpis.put("centralTotalQuantity", centralItems.stream()
                    .mapToDouble(item -> item.getQuantity().doubleValue())
                    .sum());
        }
        
        // Stock Tampon
        StockLocationId bufferLocation = stockLocationService.getLocationsByType(StockLocationType.STOCK_BUFFER)
                .stream()
                .findFirst()
                .map(com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockLocation::getId)
                .orElse(null);
        
        if (bufferLocation != null) {
            List<com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockItem> bufferItems = 
                    stockItemService.getStockItemsByLocation(bufferLocation);
            kpis.put("bufferTotalProducts", bufferItems.size());
            kpis.put("bufferTotalQuantity", bufferItems.stream()
                    .mapToDouble(item -> item.getQuantity().doubleValue())
                    .sum());
        }
        
        // Stock Mobile
        var mobileLocations = stockLocationService.getLocationsByType(StockLocationType.STOCK_MOBILE);
        kpis.put("mobileCommercialsCount", mobileLocations.size());
        
        // Alertes actives
        List<com.NTFOODS.Api_tanty.modules.stock.domain.alerte.entity.StockAlert> activeAlerts = 
                stockAlertService.getActiveAlertsByPriority();
        kpis.put("activeAlertsCount", activeAlerts.size());
        kpis.put("criticalAlertsCount", stockAlertService.getCriticalActiveAlerts().size());
        
        // Dotations en attente
        List<com.NTFOODS.Api_tanty.modules.stock.domain.dotation.entity.DotationRequest> pendingDotations = 
                dotationService.getPendingRequests();
        kpis.put("pendingDotationsCount", pendingDotations.size());
        
        kpis.put("lastUpdated", LocalDateTime.now());
        
        return kpis;
    }
    
    /**
     * Récupère les KPIs des mouvements de stock récents
     */
    public Map<String, Object> getRecentMovementsKPIs(int days) {
        Map<String, Object> kpis = new HashMap<>();
        
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        LocalDateTime endDate = LocalDateTime.now();
        
        List<com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockMovement> movements = 
                stockMovementService.getMovementsByPeriod(startDate, endDate);
        
        kpis.put("totalMovements", movements.size());
        kpis.put("movementsByType", movements.stream()
                .collect(Collectors.groupingBy(m -> m.getType().toString(), Collectors.counting())));
        
        kpis.put("totalQuantityMoved", movements.stream()
                .mapToDouble(m -> m.getQuantity().doubleValue())
                .sum());
        
        return kpis;
    }
    
    /**
     * Récupère les KPIs des commerciaux
     */
    public Map<String, Object> getCommercialsKPIs() {
        Map<String, Object> kpis = new HashMap<>();
        
        List<MobileStockTrackingService.MobileStockSummary> allMobileStock = 
                mobileStockTrackingService.getAllCommercialsMobileStock();
        
        kpis.put("totalCommercials", allMobileStock.size());
        kpis.put("totalMobileStock", allMobileStock.stream()
                .mapToDouble(summary -> summary.getTotalValue().doubleValue())
                .sum());
        
        // Commerciaux avec stock lent
        List<MobileStockTrackingService.SlowStockCommercial> slowStockCommercials = 
                mobileStockTrackingService.identifySlowStockCommercials(30, BigDecimal.valueOf(100));
        kpis.put("slowStockCommercialsCount", slowStockCommercials.size());
        
        return kpis;
    }
    
    /**
     * Récupère les KPIs des alertes
     */
    public Map<String, Object> getAlertsKPIs() {
        Map<String, Object> kpis = new HashMap<>();
        
        List<com.NTFOODS.Api_tanty.modules.stock.domain.alerte.entity.StockAlert> activeAlerts = 
                stockAlertService.getActiveAlertsByPriority();
        
        kpis.put("totalActiveAlerts", activeAlerts.size());
        
        kpis.put("alertsByPriority", activeAlerts.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getPriority().toString(), 
                        Collectors.counting())));
        
        kpis.put("alertsByType", activeAlerts.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getType().toString(), 
                        Collectors.counting())));
        
        kpis.put("unacknowledgedAlerts", stockAlertService.getUnacknowledgedActiveAlerts().size());
        
        return kpis;
    }
    
    /**
     * Récupère un résumé complet du dashboard
     */
    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        summary.put("globalStockKPIs", getGlobalStockKPIs());
        summary.put("recentMovementsKPIs", getRecentMovementsKPIs(7));
        summary.put("commercialsKPIs", getCommercialsKPIs());
        summary.put("alertsKPIs", getAlertsKPIs());
        summary.put("generatedAt", LocalDateTime.now());
        
        return summary;
    }
}
