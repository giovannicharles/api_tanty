package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.repository;

import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.jpa.StockItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * StockItemRepository - Repository pour StockItemJpaEntity
 */
@Repository
public interface StockItemRepository extends JpaRepository<StockItemJpaEntity, Long> {
    
    List<StockItemJpaEntity> findByLocationId(UUID locationId);
    
    Optional<StockItemJpaEntity> findByLocationIdAndProductId(UUID locationId, Long productId);
    
    Optional<StockItemJpaEntity> findByLocationIdAndProductSku(UUID locationId, String productSku);

    Optional<StockItemJpaEntity> findByLocationIdAndProductSkuAndPackagingType(UUID locationId, String productSku, String packagingType);

    List<StockItemJpaEntity> findAllByLocationIdAndProductSku(UUID locationId, String productSku);
    
    @Query("SELECT si FROM StockItemJpaEntity si WHERE si.locationId = :locationId AND si.quantity < :threshold")
    List<StockItemJpaEntity> findLowStockItems(@Param("locationId") UUID locationId, @Param("threshold") java.math.BigDecimal threshold);
    
    @Query("SELECT si FROM StockItemJpaEntity si WHERE si.productId = :productId")
    List<StockItemJpaEntity> findByProductId(@Param("productId") Long productId);
    
    @Query("SELECT SUM(si.quantity) FROM StockItemJpaEntity si WHERE si.locationId = :locationId")
    Optional<java.math.BigDecimal> sumQuantityByLocationId(@Param("locationId") UUID locationId);
    
    @Query("SELECT si FROM StockItemJpaEntity si WHERE si.locationId = :locationId ORDER BY si.productSku")
    List<StockItemJpaEntity> findByLocationIdOrderByProductSku(@Param("locationId") UUID locationId);
}
