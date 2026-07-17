package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.domain.audit.entity.StockAuditLog;
import com.NTFOODS.Api_tanty.modules.stock.domain.audit.repository.StockAuditLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * StockAuditLogService - Gestion du journal d'audit du stock.
 */
@Service
@Transactional
public class StockAuditLogService {

    private static final Logger log = LoggerFactory.getLogger(StockAuditLogService.class);

    private final StockAuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public StockAuditLogService(StockAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = new ObjectMapper();
    }

    public StockAuditLog log(String entityType, String entityId, String action,
                               String userMatricule, String userName,
                               Object oldValues, Object newValues, String reason) {
        String oldJson = toJson(oldValues);
        String newJson = toJson(newValues);
        StockAuditLog entry = new StockAuditLog(
                entityType, entityId, action, userMatricule, userName, oldJson, newJson, reason
        );
        StockAuditLog saved = auditLogRepository.save(entry);
        log.info("Audit log [{}] for {}:{} by {}", action, entityType, entityId, userMatricule);
        return saved;
    }

    public List<StockAuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }

    public List<StockAuditLog> getLogsForEntity(String entityType, String entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    public List<StockAuditLog> getLogsByEntityType(String entityType) {
        return auditLogRepository.findByEntityType(entityType);
    }

    public List<StockAuditLog> getLogsByUser(String userMatricule) {
        return auditLogRepository.findByUserMatricule(userMatricule);
    }

    public List<StockAuditLog> getLogsByAction(String action) {
        return auditLogRepository.findByAction(action);
    }

    public List<StockAuditLog> getLogsBetween(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetween(start, end);
    }

    private String toJson(Object value) {
        if (value == null) return null;
        if (value instanceof String) return (String) value;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize audit value: {}", e.getMessage());
            return value.toString();
        }
    }
}
