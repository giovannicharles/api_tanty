package com.NTFOODS.Api_tanty.modules.stock.infrastructure.web;

import com.NTFOODS.Api_tanty.modules.stock.application.service.ProductionBatchService;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.productionbatch.jpa.ProductionBatchJpaEntity;
import com.NTFOODS.Api_tanty.shared.infrastructure.security.SecurityUtils;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/stock/production/batches")
public class ProductionBatchController {

    private static final Logger log = LoggerFactory.getLogger(ProductionBatchController.class);

    private final ProductionBatchService productionBatchService;

    public ProductionBatchController(ProductionBatchService productionBatchService) {
        this.productionBatchService = productionBatchService;
    }

    @GetMapping
    public ResponseEntity<List<ProductionBatchJpaEntity>> getAllBatches() {
        return ResponseEntity.ok(productionBatchService.getAllBatches());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ProductionBatchJpaEntity>> getPendingBatches() {
        return ResponseEntity.ok(productionBatchService.getPendingBatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductionBatchJpaEntity> getBatch(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productionBatchService.getBatchById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/declare")
    public ResponseEntity<ProductionBatchJpaEntity> declareBatch(@RequestBody Map<String, Object> payload) {
        try {
            String matricule = SecurityUtils.getCurrentMatricule();
            String declaredByName = matricule;

            Long productId = Long.valueOf(payload.get("productId").toString());
            String productSku = (String) payload.get("productSku");
            String productName = (String) payload.get("productName");
            String productUnit = (String) payload.get("productUnit");
            BigDecimal declaredQuantityKg = new BigDecimal(payload.get("declaredQuantityKg").toString());
            BigDecimal equivalentUnits = payload.get("equivalentUnits") != null
                    ? new BigDecimal(payload.get("equivalentUnits").toString()) : null;
            LocalDate productionDate = payload.get("productionDate") != null
                    ? LocalDate.parse(payload.get("productionDate").toString()) : LocalDate.now();
            String notes = (String) payload.get("notes");
            String conditioningType = (String) payload.get("conditioningType");
            BigDecimal conditioningQty = payload.get("conditioningQty") != null
                    ? new BigDecimal(payload.get("conditioningQty").toString()) : null;

            ProductionBatchJpaEntity batch = productionBatchService.declareBatch(
                    productId, productSku, productName, productUnit,
                    declaredQuantityKg, equivalentUnits, productionDate,
                    matricule, declaredByName, notes, conditioningType, conditioningQty
            );
            return ResponseEntity.ok(batch);
        } catch (Exception e) {
            log.error("Erreur déclaration lot: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        List<ProductionBatchJpaEntity> all = productionBatchService.getAllBatches();
        long totalDeclared = all.size();
        long pending = all.stream().filter(b -> "DECLARED_BY_PRODUCTION".equals(b.getStatus())).count();
        long validated = all.stream().filter(b -> "VALIDATED_BY_STOCK".equals(b.getStatus())).count();
        long rejected = all.stream().filter(b -> "REJECTED".equals(b.getStatus())).count();
        BigDecimal totalKg = all.stream()
                .filter(b -> b.getDeclaredQuantityKg() != null)
                .reduce(BigDecimal.ZERO, (sum, b) -> sum.add(b.getDeclaredQuantityKg()), BigDecimal::add);
        BigDecimal totalUnits = all.stream()
                .filter(b -> b.getEquivalentUnits() != null)
                .reduce(BigDecimal.ZERO, (sum, b) -> sum.add(b.getEquivalentUnits()), BigDecimal::add);
        BigDecimal validatedKg = all.stream()
                .filter(b -> "VALIDATED_BY_STOCK".equals(b.getStatus()) && b.getDeclaredQuantityKg() != null)
                .reduce(BigDecimal.ZERO, (sum, b) -> sum.add(b.getDeclaredQuantityKg()), BigDecimal::add);
        BigDecimal validatedUnits = all.stream()
                .filter(b -> "VALIDATED_BY_STOCK".equals(b.getStatus()) && b.getEquivalentUnits() != null)
                .reduce(BigDecimal.ZERO, (sum, b) -> sum.add(b.getEquivalentUnits()), BigDecimal::add);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDeclared", totalDeclared);
        stats.put("pending", pending);
        stats.put("validated", validated);
        stats.put("rejected", rejected);
        stats.put("totalKg", totalKg);
        stats.put("totalUnits", totalUnits);
        stats.put("validatedKg", validatedKg);
        stats.put("validatedUnits", validatedUnits);
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/{id}/validate")
    @PreAuthorize("hasAnyAuthority('ROLE_STOCK','ROLE_ADMIN')")
    public ResponseEntity<ProductionBatchJpaEntity> validateBatch(
            @PathVariable Long id,
            @RequestParam(defaultValue = "") String notes) {
        try {
            String matricule = SecurityUtils.getCurrentMatricule();
            String validatorName = matricule;

            ProductionBatchJpaEntity batch = productionBatchService.validateBatch(
                    id, new UserId(matricule), validatorName, notes
            );
            return ResponseEntity.ok(batch);
        } catch (Exception e) {
            log.error("Erreur validation lot {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyAuthority('ROLE_STOCK','ROLE_ADMIN')")
    public ResponseEntity<ProductionBatchJpaEntity> rejectBatch(
            @PathVariable Long id,
            @RequestParam String reason) {
        try {
            String matricule = SecurityUtils.getCurrentMatricule();

            ProductionBatchJpaEntity batch = productionBatchService.rejectBatch(
                    id, new UserId(matricule), reason
            );
            return ResponseEntity.ok(batch);
        } catch (Exception e) {
            log.error("Erreur rejet lot {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
