package com.NTFOODS.Api_tanty.modules.stock.infrastructure.web;

import com.NTFOODS.Api_tanty.modules.stock.application.service.StockBatchService;
import com.NTFOODS.Api_tanty.modules.stock.domain.batch.entity.StockBatch;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock/batches")
public class StockBatchController {

    private final StockBatchService stockBatchService;

    public StockBatchController(StockBatchService stockBatchService) {
        this.stockBatchService = stockBatchService;
    }

    @GetMapping
    public ResponseEntity<List<StockBatch>> getAllBatches() {
        return ResponseEntity.ok(stockBatchService.getAllBatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockBatch> getBatch(@PathVariable Long id) {
        return stockBatchService.getBatch(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockBatch>> getBatchesByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(stockBatchService.getBatchesByProduct(productId));
    }

    @GetMapping("/sku/{productSku}")
    public ResponseEntity<List<StockBatch>> getBatchesBySku(@PathVariable String productSku) {
        return ResponseEntity.ok(stockBatchService.getBatchesBySku(productSku));
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<StockBatch>> getBatchesByLocation(@PathVariable String locationId) {
        return ResponseEntity.ok(stockBatchService.getBatchesByLocation(locationId));
    }

    @GetMapping("/expiring-soon")
    public ResponseEntity<List<StockBatch>> getExpiringSoon(@RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(stockBatchService.getExpiringSoonBatches(days));
    }

    @GetMapping("/expired")
    public ResponseEntity<List<StockBatch>> getExpired() {
        return ResponseEntity.ok(stockBatchService.getExpiredBatches());
    }

    @PostMapping
    public ResponseEntity<StockBatch> createBatch(@RequestBody Map<String, Object> payload) {
        String batchNumber = (String) payload.get("batchNumber");
        Long productId = Long.valueOf(payload.get("productId").toString());
        String productSku = (String) payload.get("productSku");
        String productName = (String) payload.get("productName");
        String supplierName = (String) payload.get("supplierName");
        LocalDate manufactureDate = parseDate((String) payload.get("manufactureDate"));
        LocalDate expiryDate = parseDate((String) payload.get("expiryDate"));
        BigDecimal initialQuantity = new BigDecimal(payload.get("initialQuantity").toString());
        String locationId = (String) payload.get("locationId");
        String notes = (String) payload.get("notes");

        StockBatch batch = stockBatchService.createBatch(
                batchNumber, productId, productSku, productName, supplierName,
                manufactureDate, expiryDate, initialQuantity, locationId, notes
        );
        return ResponseEntity.ok(batch);
    }

    @PostMapping("/consume")
    public ResponseEntity<Void> consume(@RequestBody Map<String, Object> payload) {
        String productSku = (String) payload.get("productSku");
        String locationId = (String) payload.get("locationId");
        BigDecimal quantity = new BigDecimal(payload.get("quantity").toString());
        stockBatchService.consumeQuantity(productSku, locationId, quantity);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<StockBatch> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(stockBatchService.updateBatchStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBatch(@PathVariable Long id) {
        stockBatchService.deleteBatch(id);
        return ResponseEntity.ok().build();
    }

    private LocalDate parseDate(String value) {
        return value != null && !value.isBlank() ? LocalDate.parse(value) : null;
    }
}
