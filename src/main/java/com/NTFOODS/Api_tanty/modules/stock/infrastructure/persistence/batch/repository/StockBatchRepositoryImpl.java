package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.batch.repository;

import com.NTFOODS.Api_tanty.modules.stock.domain.batch.entity.StockBatch;
import com.NTFOODS.Api_tanty.modules.stock.domain.batch.repository.StockBatchRepository;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.batch.jpa.StockBatchJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.batch.jpa.StockBatchJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class StockBatchRepositoryImpl implements StockBatchRepository {

    private final StockBatchJpaRepository jpaRepository;

    public StockBatchRepositoryImpl(StockBatchJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public StockBatch save(StockBatch batch) {
        StockBatchJpaEntity entity = toJpa(batch);
        StockBatchJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<StockBatch> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<StockBatch> findByBatchNumber(String batchNumber) {
        return jpaRepository.findByBatchNumber(batchNumber).map(this::toDomain);
    }

    @Override
    public List<StockBatch> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<StockBatch> findByProductId(Long productId) {
        return jpaRepository.findByProductId(productId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<StockBatch> findByProductSku(String productSku) {
        return jpaRepository.findByProductSku(productSku).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<StockBatch> findByLocationId(String locationId) {
        return jpaRepository.findByLocationId(locationId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<StockBatch> findActiveByProductSkuAndLocation(String productSku, String locationId) {
        return jpaRepository.findActiveByProductSkuAndLocationOrderByExpiry(productSku, locationId)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<StockBatch> findExpiringBefore(LocalDate date) {
        return jpaRepository.findExpiringBefore(date).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<StockBatch> findExpired() {
        return jpaRepository.findExpiredAvailable().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private StockBatchJpaEntity toJpa(StockBatch batch) {
        StockBatchJpaEntity entity = new StockBatchJpaEntity(
                batch.getBatchNumber(),
                batch.getProductId(),
                batch.getProductSku(),
                batch.getProductName(),
                batch.getSupplierName(),
                batch.getManufactureDate(),
                batch.getExpiryDate(),
                batch.getInitialQuantity(),
                batch.getRemainingQuantity(),
                batch.getLocationId(),
                batch.getStatus(),
                batch.getNotes()
        );
        if (batch.getId() != null) {
            entity.setId(batch.getId());
        }
        if (batch.getCreatedAt() != null) {
            entity.setCreatedAt(batch.getCreatedAt());
        }
        entity.setLastMovementAt(batch.getLastMovementAt());
        return entity;
    }

    private StockBatch toDomain(StockBatchJpaEntity entity) {
        StockBatch batch = new StockBatch(
                entity.getBatchNumber(),
                entity.getProductId(),
                entity.getProductSku(),
                entity.getProductName(),
                entity.getSupplierName(),
                entity.getManufactureDate(),
                entity.getExpiryDate(),
                entity.getInitialQuantity(),
                entity.getRemainingQuantity(),
                entity.getLocationId(),
                entity.getStatus(),
                entity.getNotes()
        );
        batch.setId(entity.getId());
        batch.setCreatedAt(entity.getCreatedAt());
        batch.setLastMovementAt(entity.getLastMovementAt());
        return batch;
    }
}
