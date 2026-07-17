package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.repository;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockMovementType;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.jpa.StockMovementJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * StockMovementRepository - Repository pour StockMovementJpaEntity
 */
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovementJpaEntity, Long> {
    
    List<StockMovementJpaEntity> findByFromLocationId(UUID fromLocationId);
    
    List<StockMovementJpaEntity> findByToLocationId(UUID toLocationId);
    
    List<StockMovementJpaEntity> findByProductId(Long productId);
    
    List<StockMovementJpaEntity> findByStatus(String status);
    
    List<StockMovementJpaEntity> findByType(StockMovementType type);
    
    @Query("SELECT sm FROM StockMovementJpaEntity sm WHERE sm.status = 'PENDING' ORDER BY sm.requestedAt")
    List<StockMovementJpaEntity> findPendingMovements();
    
    @Query("SELECT sm FROM StockMovementJpaEntity sm WHERE sm.fromLocationId = :locationId OR sm.toLocationId = :locationId ORDER BY sm.requestedAt DESC")
    List<StockMovementJpaEntity> findByLocationIdOrderByRequestedAtDesc(@Param("locationId") UUID locationId);
    
    @Query("SELECT sm FROM StockMovementJpaEntity sm WHERE sm.requestedAt BETWEEN :startDate AND :endDate")
    List<StockMovementJpaEntity> findByRequestedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT sm FROM StockMovementJpaEntity sm WHERE sm.requestedBy = :userId")
    List<StockMovementJpaEntity> findByRequestedBy(@Param("userId") UUID userId);
    
    @Query("SELECT sm FROM StockMovementJpaEntity sm WHERE sm.validatedBy = :userId")
    List<StockMovementJpaEntity> findByValidatedBy(@Param("userId") UUID userId);
}
