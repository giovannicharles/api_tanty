package com.NTFOODS.Api_tanty.modules.stock.domain.audit.repository;

import com.NTFOODS.Api_tanty.modules.stock.domain.audit.entity.StockAuditLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StockAuditLogRepository {
    StockAuditLog save(StockAuditLog log);
    Optional<StockAuditLog> findById(Long id);
    List<StockAuditLog> findAll();
    List<StockAuditLog> findByEntityTypeAndEntityId(String entityType, String entityId);
    List<StockAuditLog> findByEntityType(String entityType);
    List<StockAuditLog> findByUserMatricule(String userMatricule);
    List<StockAuditLog> findByAction(String action);
    List<StockAuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
