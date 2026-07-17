package com.NTFOODS.Api_tanty.modules.stock.infrastructure.web;

import com.NTFOODS.Api_tanty.modules.stock.application.service.StockThresholdService;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.modules.stock.domain.seuil.entity.StockThreshold;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock/thresholds")
public class StockThresholdController {

    private final StockThresholdService stockThresholdService;

    public StockThresholdController(StockThresholdService stockThresholdService) {
        this.stockThresholdService = stockThresholdService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_STOCK','ROLE_ADMIN','ROLE_DIRECTION')")
    public ResponseEntity<List<StockThreshold>> getAllThresholds() {
        return ResponseEntity.ok(stockThresholdService.getAllThresholds());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_STOCK','ROLE_ADMIN')")
    public ResponseEntity<StockThreshold> createThreshold(@RequestBody CreateThresholdRequest request) {
        StockLocationId stockLocationId = new StockLocationId(UUID.fromString(request.locationId));
        StockThreshold threshold = stockThresholdService.createThreshold(
                stockLocationId, request.productId, request.productSku, request.minimumThreshold,
                request.maximumThreshold, request.reorderThreshold, request.reorderQuantity, request.notes);

        return ResponseEntity.ok(threshold);
    }

    public static class CreateThresholdRequest {
        public String locationId;
        public Long productId;
        public String productSku;
        public BigDecimal minimumThreshold;
        public BigDecimal maximumThreshold;
        public BigDecimal reorderThreshold;
        public BigDecimal reorderQuantity;
        public String notes;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_STOCK','ROLE_ADMIN')")
    public ResponseEntity<StockThreshold> updateThreshold(
            @PathVariable Long id,
            @RequestBody UpdateThresholdRequest request) {

        StockThreshold threshold = stockThresholdService.updateThreshold(
                id, request.minimumThreshold, request.maximumThreshold,
                request.reorderThreshold, request.reorderQuantity, request.notes);

        return ResponseEntity.ok(threshold);
    }

    public static class UpdateThresholdRequest {
        public BigDecimal minimumThreshold;
        public BigDecimal maximumThreshold;
        public BigDecimal reorderThreshold;
        public BigDecimal reorderQuantity;
        public String notes;
    }

    @GetMapping("/location/{locationId}/product/{productId}")
    @PreAuthorize("hasAnyAuthority('ROLE_STOCK','ROLE_ADMIN','ROLE_DIRECTION')")
    public ResponseEntity<StockThreshold> getThreshold(@PathVariable UUID locationId, @PathVariable Long productId) {
        StockLocationId stockLocationId = new StockLocationId(locationId);
        return stockThresholdService.getThreshold(stockLocationId, productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/location/{locationId}/sku/{productSku}")
    public ResponseEntity<StockThreshold> getThresholdBySku(@PathVariable UUID locationId, @PathVariable String productSku) {
        StockLocationId stockLocationId = new StockLocationId(locationId);
        return stockThresholdService.getThresholdBySku(stockLocationId, productSku)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/location/{locationId}")
    @PreAuthorize("hasAnyAuthority('ROLE_STOCK','ROLE_ADMIN','ROLE_DIRECTION')")
    public ResponseEntity<List<StockThreshold>> getThresholdsByLocation(@PathVariable UUID locationId) {
        StockLocationId stockLocationId = new StockLocationId(locationId);
        List<StockThreshold> thresholds = stockThresholdService.getThresholdsByLocation(stockLocationId);
        return ResponseEntity.ok(thresholds);
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyAuthority('ROLE_STOCK','ROLE_ADMIN','ROLE_DIRECTION')")
    public ResponseEntity<List<StockThreshold>> getThresholdsByProduct(@PathVariable Long productId) {
        List<StockThreshold> thresholds = stockThresholdService.getThresholdsByProduct(productId);
        return ResponseEntity.ok(thresholds);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_STOCK','ROLE_ADMIN')")
    public ResponseEntity<Void> deleteThreshold(@PathVariable Long id) {
        stockThresholdService.deleteThreshold(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/check")
    @PreAuthorize("hasAnyAuthority('ROLE_STOCK','ROLE_ADMIN')")
    public ResponseEntity<Void> checkAllThresholds() {
        stockThresholdService.checkAllThresholds();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/location/{locationId}/sku/{productSku}/reorder-quantity")
    public ResponseEntity<BigDecimal> calculateRecommendedReorderQuantity(
            @PathVariable UUID locationId,
            @PathVariable String productSku) {
        
        StockLocationId stockLocationId = new StockLocationId(locationId);
        BigDecimal quantity = stockThresholdService.calculateRecommendedReorderQuantity(stockLocationId, productSku);
        return ResponseEntity.ok(quantity);
    }
}
