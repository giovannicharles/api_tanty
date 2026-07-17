package com.NTFOODS.Api_tanty.modules.stock.infrastructure.web;

import com.NTFOODS.Api_tanty.modules.stock.application.service.StockAuditLogService;
import com.NTFOODS.Api_tanty.modules.stock.domain.audit.entity.StockAuditLog;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock/audit-logs")
public class StockAuditLogController {

    private final StockAuditLogService auditLogService;

    public StockAuditLogController(StockAuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<StockAuditLog>> getAllLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<StockAuditLog>> getLogsForEntity(
            @PathVariable String entityType, @PathVariable String entityId) {
        return ResponseEntity.ok(auditLogService.getLogsForEntity(entityType, entityId));
    }

    @GetMapping("/entity-type/{entityType}")
    public ResponseEntity<List<StockAuditLog>> getLogsByEntityType(@PathVariable String entityType) {
        return ResponseEntity.ok(auditLogService.getLogsByEntityType(entityType));
    }

    @GetMapping("/user/{userMatricule}")
    public ResponseEntity<List<StockAuditLog>> getLogsByUser(@PathVariable String userMatricule) {
        return ResponseEntity.ok(auditLogService.getLogsByUser(userMatricule));
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<List<StockAuditLog>> getLogsByAction(@PathVariable String action) {
        return ResponseEntity.ok(auditLogService.getLogsByAction(action));
    }

    @GetMapping("/between")
    public ResponseEntity<List<StockAuditLog>> getLogsBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(auditLogService.getLogsBetween(start, end));
    }

    @PostMapping
    public ResponseEntity<StockAuditLog> createLog(@RequestBody Map<String, Object> payload) {
        String entityType = (String) payload.get("entityType");
        String entityId = payload.get("entityId") != null ? payload.get("entityId").toString() : null;
        String action = (String) payload.get("action");
        String userMatricule = (String) payload.get("userMatricule");
        String userName = (String) payload.get("userName");
        Object oldValues = payload.get("oldValues");
        Object newValues = payload.get("newValues");
        String reason = (String) payload.get("reason");

        StockAuditLog log = auditLogService.log(
                entityType, entityId, action, userMatricule, userName, oldValues, newValues, reason
        );
        return ResponseEntity.ok(log);
    }
}
