package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.audit.repository;

import com.NTFOODS.Api_tanty.modules.stock.domain.audit.entity.StockAuditLog;
import com.NTFOODS.Api_tanty.modules.stock.domain.audit.repository.StockAuditLogRepository;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.audit.jpa.StockAuditLogJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.audit.jpa.StockAuditLogJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class StockAuditLogRepositoryImpl implements StockAuditLogRepository {

    private final StockAuditLogJpaRepository jpaRepository;

    public StockAuditLogRepositoryImpl(StockAuditLogJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public StockAuditLog save(StockAuditLog log) {
        StockAuditLogJpaEntity entity = toJpa(log);
        StockAuditLogJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<StockAuditLog> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<StockAuditLog> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<StockAuditLog> findByEntityTypeAndEntityId(String entityType, String entityId) {
        return jpaRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<StockAuditLog> findByEntityType(String entityType) {
        return jpaRepository.findByEntityTypeOrderByTimestampDesc(entityType)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<StockAuditLog> findByUserMatricule(String userMatricule) {
        return jpaRepository.findByUserMatriculeOrderByTimestampDesc(userMatricule)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<StockAuditLog> findByAction(String action) {
        return jpaRepository.findByActionOrderByTimestampDesc(action)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<StockAuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end) {
        return jpaRepository.findByTimestampBetweenOrderByTimestampDesc(start, end)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    private StockAuditLogJpaEntity toJpa(StockAuditLog log) {
        StockAuditLogJpaEntity entity = new StockAuditLogJpaEntity(
                log.getEntityType(),
                log.getEntityId(),
                log.getAction(),
                log.getUserMatricule(),
                log.getUserName(),
                log.getOldValues(),
                log.getNewValues(),
                log.getReason()
        );
        if (log.getId() != null) {
            entity.setId(log.getId());
        }
        if (log.getTimestamp() != null) {
            entity.setTimestamp(log.getTimestamp());
        }
        return entity;
    }

    private StockAuditLog toDomain(StockAuditLogJpaEntity entity) {
        StockAuditLog log = new StockAuditLog(
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getAction(),
                entity.getUserMatricule(),
                entity.getUserName(),
                entity.getOldValues(),
                entity.getNewValues(),
                entity.getReason()
        );
        log.setId(entity.getId());
        log.setTimestamp(entity.getTimestamp());
        return log;
    }
}
