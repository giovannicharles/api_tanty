package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.batch.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockBatchJpaRepository extends JpaRepository<StockBatchJpaEntity, Long> {

    Optional<StockBatchJpaEntity> findByBatchNumber(String batchNumber);

    List<StockBatchJpaEntity> findByProductId(Long productId);

    List<StockBatchJpaEntity> findByProductSku(String productSku);

    List<StockBatchJpaEntity> findByLocationId(String locationId);

    @Query("SELECT b FROM StockBatchJpaEntity b WHERE b.productSku = :productSku AND b.locationId = :locationId AND b.status = 'AVAILABLE' AND b.remainingQuantity > 0 ORDER BY b.expiryDate ASC, b.createdAt ASC")
    List<StockBatchJpaEntity> findActiveByProductSkuAndLocationOrderByExpiry(@Param("productSku") String productSku, @Param("locationId") String locationId);

    @Query("SELECT b FROM StockBatchJpaEntity b WHERE b.expiryDate <= :date AND b.expiryDate >= CURRENT_DATE AND b.status = 'AVAILABLE' ORDER BY b.expiryDate ASC")
    List<StockBatchJpaEntity> findExpiringBefore(@Param("date") LocalDate date);

    @Query("SELECT b FROM StockBatchJpaEntity b WHERE b.expiryDate < CURRENT_DATE AND b.status = 'AVAILABLE' ORDER BY b.expiryDate ASC")
    List<StockBatchJpaEntity> findExpiredAvailable();
}
