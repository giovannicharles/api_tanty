package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.seuil.repository;

import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.seuil.jpa.StockThresholdJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * StockThresholdRepository - Repository pour StockThresholdJpaEntity
 */
@Repository
public interface StockThresholdRepository extends JpaRepository<StockThresholdJpaEntity, Long> {
    
    Optional<StockThresholdJpaEntity> findByLocationIdAndProductId(UUID locationId, Long productId);
    
    Optional<StockThresholdJpaEntity> findByLocationIdAndProductSku(UUID locationId, String productSku);
    
    List<StockThresholdJpaEntity> findByLocationId(UUID locationId);
    
    List<StockThresholdJpaEntity> findByProductId(Long productId);
    
    @Query("SELECT st FROM StockThresholdJpaEntity st WHERE st.locationId = :locationId ORDER BY st.productSku")
    List<StockThresholdJpaEntity> findByLocationIdOrderByProductSku(@Param("locationId") UUID locationId);
    
    @Query("SELECT st FROM StockThresholdJpaEntity st WHERE st.productId = :productId ORDER BY st.locationId")
    List<StockThresholdJpaEntity> findByProductIdOrderByLocationId(@Param("productId") Long productId);
}
