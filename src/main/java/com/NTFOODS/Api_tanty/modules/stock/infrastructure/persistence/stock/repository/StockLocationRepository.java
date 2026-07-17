package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.repository;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.jpa.StockLocationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * StockLocationRepository - Repository pour StockLocationJpaEntity
 */
@Repository
public interface StockLocationRepository extends JpaRepository<StockLocationJpaEntity, Long> {
    
    Optional<StockLocationJpaEntity> findByLocationId(UUID locationId);
    
    List<StockLocationJpaEntity> findByType(StockLocationType type);
    
    Optional<StockLocationJpaEntity> findByAssignedUserId(String assignedUserId);
    
    @Query("SELECT sl FROM StockLocationJpaEntity sl WHERE sl.type = :type AND sl.assignedUserId IS NULL")
    List<StockLocationJpaEntity> findAvailableLocationsByType(@Param("type") StockLocationType type);
    
    @Query("SELECT sl FROM StockLocationJpaEntity sl WHERE sl.type = :type ORDER BY sl.name")
    List<StockLocationJpaEntity> findByTypeOrderByName(@Param("type") StockLocationType type);

    List<StockLocationJpaEntity> findByManagerId(String managerId);

    List<StockLocationJpaEntity> findByTypeAndActiveTrue(StockLocationType type);
}
