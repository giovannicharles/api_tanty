package com.NTFOODS.Api_tanty.modules.stock.infrastructure.web;

import com.NTFOODS.Api_tanty.modules.stock.application.dto.BufferValuationResponse;
import com.NTFOODS.Api_tanty.modules.stock.application.service.StockValuationService;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock/valuation")
public class StockValuationController {

    private final StockValuationService stockValuationService;

    public StockValuationController(StockValuationService stockValuationService) {
        this.stockValuationService = stockValuationService;
    }

    @PostMapping("/location/{locationId}")
    public ResponseEntity<BigDecimal> calculateStockValue(
            @PathVariable UUID locationId,
            @RequestBody Map<String, BigDecimal> unitPrices) {
        
        StockLocationId stockLocationId = new StockLocationId(locationId);
        BigDecimal value = stockValuationService.calculateStockValue(stockLocationId, unitPrices);
        return ResponseEntity.ok(value);
    }

    @PostMapping("/central")
    public ResponseEntity<BigDecimal> calculateCentralStockValue(@RequestBody Map<String, BigDecimal> unitPrices) {
        BigDecimal value = stockValuationService.calculateCentralStockValue(unitPrices);
        return ResponseEntity.ok(value);
    }

    @PostMapping("/buffer")
    public ResponseEntity<BigDecimal> calculateBufferStockValue(@RequestBody Map<String, BigDecimal> unitPrices) {
        BigDecimal value = stockValuationService.calculateBufferStockValue(unitPrices);
        return ResponseEntity.ok(value);
    }

    @GetMapping("/buffer/auto")
    public ResponseEntity<BufferValuationResponse> calculateBufferValuationAuto() {
        BufferValuationResponse response = stockValuationService.calculateBufferValuationAuto();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/central/auto")
    public ResponseEntity<BufferValuationResponse> calculateCentralValuationAuto() {
        BufferValuationResponse response = stockValuationService.calculateLocationValuationAuto(
                com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType.STOCK_CENTRAL);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mobile/auto")
    public ResponseEntity<BufferValuationResponse> calculateMobileValuationAuto() {
        BufferValuationResponse response = stockValuationService.calculateLocationValuationAuto(
                com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType.STOCK_MOBILE);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/total/auto")
    public ResponseEntity<StockValuationService.StockValuationSummary> calculateTotalValuationAuto() {
        StockValuationService.StockValuationSummary summary = stockValuationService.calculateTotalValuationAuto();
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/mobile")
    public ResponseEntity<BigDecimal> calculateMobileStockValue(@RequestBody Map<String, BigDecimal> unitPrices) {
        BigDecimal value = stockValuationService.calculateMobileStockValue(unitPrices);
        return ResponseEntity.ok(value);
    }

    @PostMapping("/total")
    public ResponseEntity<StockValuationService.StockValuationSummary> calculateTotalStockValue(
            @RequestBody ValuationRequest request) {

        StockValuationService.StockValuationSummary summary =
                stockValuationService.calculateTotalStockValue(request.unitPrices);
        return ResponseEntity.ok(summary);
    }

    public static class ValuationRequest {
        public Map<String, BigDecimal> unitPrices;
    }

    @PostMapping("/location/{locationId}/category")
    public ResponseEntity<Map<String, BigDecimal>> calculateValueByCategory(
            @PathVariable UUID locationId,
            @RequestBody Map<String, Object> request) {
        
        @SuppressWarnings("unchecked")
        Map<String, BigDecimal> unitPrices = (Map<String, BigDecimal>) request.get("unitPrices");
        @SuppressWarnings("unchecked")
        Map<String, String> productCategories = (Map<String, String>) request.get("productCategories");
        
        StockLocationId stockLocationId = new StockLocationId(locationId);
        Map<String, BigDecimal> valueByCategory = stockValuationService.calculateValueByCategory(
                stockLocationId, unitPrices, productCategories);
        
        return ResponseEntity.ok(valueByCategory);
    }

    @PostMapping("/location/{locationId}/storage-cost")
    public ResponseEntity<BigDecimal> calculateStorageCost(
            @PathVariable UUID locationId,
            @RequestParam BigDecimal storageCostRate,
            @RequestBody Map<String, BigDecimal> unitPrices) {
        
        StockLocationId stockLocationId = new StockLocationId(locationId);
        BigDecimal cost = stockValuationService.calculateStorageCost(
                stockLocationId, unitPrices, storageCostRate);
        return ResponseEntity.ok(cost);
    }
}
