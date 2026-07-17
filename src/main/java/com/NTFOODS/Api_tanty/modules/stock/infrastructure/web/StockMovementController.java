package com.NTFOODS.Api_tanty.modules.stock.infrastructure.web;

import com.NTFOODS.Api_tanty.modules.stock.application.service.StockMovementService;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockMovement;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockMovementType;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock/movements")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @PostMapping
    public ResponseEntity<StockMovement> createMovement(
            @RequestParam StockMovementType type,
            @RequestParam(required = false) UUID fromLocationId,
            @RequestParam(required = false) UUID toLocationId,
            @RequestParam Long productId,
            @RequestParam String productSku,
            @RequestParam String packagingType,
            @RequestParam BigDecimal quantity,
            @RequestParam BigDecimal quantityPerCarton,
            @RequestParam UUID requestedBy,
            @RequestParam String referenceNumber,
            @RequestParam(required = false) String notes) {
        
        StockLocationId fromLoc = fromLocationId != null ? new StockLocationId(fromLocationId) : null;
        StockLocationId toLoc = toLocationId != null ? new StockLocationId(toLocationId) : null;
        UserId userId = new UserId(requestedBy.toString());
        
        StockMovement movement = stockMovementService.createMovement(
                type, fromLoc, toLoc, productId, productSku, packagingType,
                quantity, quantityPerCarton, userId, referenceNumber, notes);
        
        return ResponseEntity.ok(movement);
    }

    @PostMapping("/{id}/validate")
    public ResponseEntity<Void> validateMovement(@PathVariable Long id, @RequestParam UUID validatedBy) {
        UserId userId = new UserId(validatedBy.toString());
        stockMovementService.validateMovement(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelMovement(@PathVariable Long id, @RequestParam String reason) {
        stockMovementService.cancelMovement(id, reason);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<StockMovement>> getAllMovements() {
        List<StockMovement> movements = stockMovementService.getAllMovements();
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<StockMovement>> getPendingMovements() {
        List<StockMovement> movements = stockMovementService.getPendingMovements();
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<StockMovement>> getMovementsByLocation(@PathVariable UUID locationId) {
        StockLocationId stockLocationId = new StockLocationId(locationId);
        List<StockMovement> movements = stockMovementService.getMovementsByLocation(stockLocationId);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<StockMovement>> getMovementsByType(@PathVariable StockMovementType type) {
        List<StockMovement> movements = stockMovementService.getMovementsByType(type);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/period")
    public ResponseEntity<List<StockMovement>> getMovementsByPeriod(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<StockMovement> movements = stockMovementService.getMovementsByPeriod(startDate, endDate);
        return ResponseEntity.ok(movements);
    }
}
