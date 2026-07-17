package com.NTFOODS.Api_tanty.modules.stock.infrastructure.web;

import com.NTFOODS.Api_tanty.modules.stock.application.service.StockAlertService;
import com.NTFOODS.Api_tanty.modules.stock.domain.alerte.entity.StockAlert;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock/alerts")
public class StockAlertController {

    private final StockAlertService stockAlertService;

    public StockAlertController(StockAlertService stockAlertService) {
        this.stockAlertService = stockAlertService;
    }

    @PostMapping("/low-stock")
    public ResponseEntity<StockAlert> createLowStockAlert(
            @RequestParam UUID locationId,
            @RequestParam Long productId,
            @RequestParam String productSku,
            @RequestParam String productName,
            @RequestParam BigDecimal currentQuantity,
            @RequestParam BigDecimal threshold) {
        
        StockLocationId stockLocationId = new StockLocationId(locationId);
        StockAlert alert = stockAlertService.createLowStockAlert(
                stockLocationId, productId, productSku, productName, currentQuantity, threshold);
        
        return ResponseEntity.ok(alert);
    }

    @PostMapping("/critical-stock")
    public ResponseEntity<StockAlert> createCriticalStockAlert(
            @RequestParam UUID locationId,
            @RequestParam Long productId,
            @RequestParam String productSku,
            @RequestParam String productName,
            @RequestParam BigDecimal currentQuantity,
            @RequestParam BigDecimal threshold) {
        
        StockLocationId stockLocationId = new StockLocationId(locationId);
        StockAlert alert = stockAlertService.createCriticalStockAlert(
                stockLocationId, productId, productSku, productName, currentQuantity, threshold);
        
        return ResponseEntity.ok(alert);
    }

    @PostMapping("/buffer-insufficient")
    public ResponseEntity<StockAlert> createBufferInsufficientAlert(
            @RequestParam UUID locationId,
            @RequestParam Long productId,
            @RequestParam String productSku,
            @RequestParam String productName,
            @RequestParam BigDecimal currentQuantity,
            @RequestParam BigDecimal requiredQuantity) {
        
        StockLocationId stockLocationId = new StockLocationId(locationId);
        StockAlert alert = stockAlertService.createBufferInsufficientAlert(
                stockLocationId, productId, productSku, productName, currentQuantity, requiredQuantity);
        
        return ResponseEntity.ok(alert);
    }

    @PostMapping("/{id}/acknowledge")
    public ResponseEntity<StockAlert> acknowledgeAlert(@PathVariable Long id, @RequestParam String userId) {
        UserId user = new UserId(userId);
        StockAlert alert = stockAlertService.acknowledgeAlert(id, user);
        return ResponseEntity.ok(alert);
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<StockAlert> resolveAlert(@PathVariable Long id) {
        StockAlert alert = stockAlertService.resolveAlert(id);
        return ResponseEntity.ok(alert);
    }

    @GetMapping("/active/priority")
    public ResponseEntity<List<StockAlert>> getActiveAlertsByPriority() {
        List<StockAlert> alerts = stockAlertService.getActiveAlertsByPriority();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/active/critical")
    public ResponseEntity<List<StockAlert>> getCriticalActiveAlerts() {
        List<StockAlert> alerts = stockAlertService.getCriticalActiveAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/active/unacknowledged")
    public ResponseEntity<List<StockAlert>> getUnacknowledgedActiveAlerts() {
        List<StockAlert> alerts = stockAlertService.getUnacknowledgedActiveAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<StockAlert>> getAlertsByLocation(@PathVariable UUID locationId) {
        StockLocationId stockLocationId = new StockLocationId(locationId);
        List<StockAlert> alerts = stockAlertService.getAlertsByLocation(stockLocationId);
        return ResponseEntity.ok(alerts);
    }

    @PostMapping("/check-thresholds")
    public ResponseEntity<Void> checkStockThresholds() {
        stockAlertService.checkStockThresholds();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/expiration")
    public ResponseEntity<StockAlert> createExpirationAlert(
            @RequestParam UUID locationId,
            @RequestParam Long productId,
            @RequestParam String productSku,
            @RequestParam String productName,
            @RequestParam StockAlert.AlertType type,
            @RequestParam BigDecimal affectedQuantity,
            @RequestParam int daysToExpiry) {
        StockLocationId stockLocationId = new StockLocationId(locationId);
        StockAlert alert = stockAlertService.createExpirationAlert(
                stockLocationId, productId, productSku, productName, type, affectedQuantity, daysToExpiry);
        return ResponseEntity.ok(alert);
    }

    @PostMapping("/slow-rotation")
    public ResponseEntity<StockAlert> createSlowRotationAlert(
            @RequestParam UUID locationId,
            @RequestParam Long productId,
            @RequestParam String productSku,
            @RequestParam String productName,
            @RequestParam BigDecimal currentQuantity,
            @RequestParam int daysInactive) {
        StockLocationId stockLocationId = new StockLocationId(locationId);
        StockAlert alert = stockAlertService.createSlowRotationAlert(
                stockLocationId, productId, productSku, productName, currentQuantity, daysInactive);
        return ResponseEntity.ok(alert);
    }

    @PostMapping("/anomaly")
    public ResponseEntity<StockAlert> createAnomalyAlert(
            @RequestParam UUID locationId,
            @RequestParam Long productId,
            @RequestParam String productSku,
            @RequestParam String productName,
            @RequestParam BigDecimal expected,
            @RequestParam BigDecimal actual) {
        StockLocationId stockLocationId = new StockLocationId(locationId);
        StockAlert alert = stockAlertService.createAnomalyAlert(
                stockLocationId, productId, productSku, productName, expected, actual);
        return ResponseEntity.ok(alert);
    }

    @PostMapping("/check-batch-expiry")
    public ResponseEntity<Void> checkBatchExpiryAlerts() {
        stockAlertService.checkBatchExpiryAlerts();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/check-no-movement")
    public ResponseEntity<Void> checkNoMovementAlerts() {
        stockAlertService.checkNoMovementAlerts();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/check-anomalies")
    public ResponseEntity<Void> checkAnomalies() {
        stockAlertService.checkAnomalies();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/performance")
    public ResponseEntity<java.util.Map<String, Object>> getPerformanceStats() {
        var all = stockAlertService.getAllAlerts();
        long total = all.size();
        long active = all.stream().filter(a -> "ACTIVE".equals(a.getStatus())).count();
        long acknowledged = all.stream().filter(a -> "ACKNOWLEDGED".equals(a.getStatus())).count();
        long resolved = all.stream().filter(a -> "RESOLVED".equals(a.getStatus())).count();
        long critical = all.stream().filter(a -> a.getPriority() == StockAlert.AlertPriority.CRITICAL && "ACTIVE".equals(a.getStatus())).count();
        
        double resolutionRate = total > 0 ? (resolved * 100.0) / total : 0;
        double acknowledgmentRate = total > 0 ? ((acknowledged + resolved) * 100.0) / total : 0;
        
        java.util.Map<String, Object> stats = new java.util.LinkedHashMap<>();
        stats.put("totalAlerts", total);
        stats.put("activeAlerts", active);
        stats.put("acknowledgedAlerts", acknowledged);
        stats.put("resolvedAlerts", resolved);
        stats.put("criticalActive", critical);
        stats.put("resolutionRate", Math.round(resolutionRate * 100) / 100.0);
        stats.put("acknowledgmentRate", Math.round(acknowledgmentRate * 100) / 100.0);
        stats.put("ruptureRisk", active > 0 ? "ÉLEVÉ" : "FAIBLE");
        
        return ResponseEntity.ok(stats);
    }
}
