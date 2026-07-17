package com.NTFOODS.Api_tanty.modules.stock.domain.batch.repository;

import com.NTFOODS.Api_tanty.modules.stock.domain.batch.entity.StockBatch;

import java.util.List;
import java.util.Optional;

public interface StockBatchRepository {
    StockBatch save(StockBatch batch);
    Optional<StockBatch> findById(Long id);
    Optional<StockBatch> findByBatchNumber(String batchNumber);
    List<StockBatch> findAll();
    List<StockBatch> findByProductId(Long productId);
    List<StockBatch> findByProductSku(String productSku);
    List<StockBatch> findByLocationId(String locationId);
    List<StockBatch> findActiveByProductSkuAndLocation(String productSku, String locationId);
    List<StockBatch> findExpiringBefore(java.time.LocalDate date);
    List<StockBatch> findExpired();
}
