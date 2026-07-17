package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.audit.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockAuditLogJpaRepository extends JpaRepository<StockAuditLogJpaEntity, Long> {

    List<StockAuditLogJpaEntity> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, String entityId);

    List<StockAuditLogJpaEntity> findByEntityTypeOrderByTimestampDesc(String entityType);

    List<StockAuditLogJpaEntity> findByUserMatriculeOrderByTimestampDesc(String userMatricule);

    List<StockAuditLogJpaEntity> findByActionOrderByTimestampDesc(String action);

    List<StockAuditLogJpaEntity> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end);
}
