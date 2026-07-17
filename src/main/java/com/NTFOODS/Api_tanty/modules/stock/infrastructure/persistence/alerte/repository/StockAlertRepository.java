package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.alerte.repository;

import com.NTFOODS.Api_tanty.modules.stock.domain.alerte.entity.StockAlert;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.alerte.jpa.StockAlertJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * StockAlertRepository - Repository pour StockAlertJpaEntity
 */
@Repository
public interface StockAlertRepository extends JpaRepository<StockAlertJpaEntity, Long> {
    
    List<StockAlertJpaEntity> findByLocationId(UUID locationId);
    
    List<StockAlertJpaEntity> findByProductId(Long productId);
    
    List<StockAlertJpaEntity> findByStatus(String status);
    
    List<StockAlertJpaEntity> findByPriority(StockAlert.AlertPriority priority);
    
    List<StockAlertJpaEntity> findByType(StockAlert.AlertType type);
    
    @Query("SELECT sa FROM StockAlertJpaEntity sa WHERE sa.status = 'ACTIVE' ORDER BY sa.priority DESC, sa.createdAt")
    List<StockAlertJpaEntity> findActiveAlertsOrderByPriority();
    
    @Query("SELECT sa FROM StockAlertJpaEntity sa WHERE sa.status = 'ACTIVE' AND sa.priority = 'CRITICAL'")
    List<StockAlertJpaEntity> findCriticalActiveAlerts();
    
    @Query("SELECT sa FROM StockAlertJpaEntity sa WHERE sa.acknowledged = false AND sa.status = 'ACTIVE'")
    List<StockAlertJpaEntity> findUnacknowledgedActiveAlerts();
    
    @Query("SELECT sa FROM StockAlertJpaEntity sa WHERE sa.createdAt BETWEEN :startDate AND :endDate")
    List<StockAlertJpaEntity> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(sa) FROM StockAlertJpaEntity sa WHERE sa.status = 'ACTIVE' AND sa.locationId = :locationId")
    long countActiveAlertsByLocation(@Param("locationId") UUID locationId);
    
    @Query("SELECT sa FROM StockAlertJpaEntity sa WHERE sa.productId = :productId AND sa.status = 'ACTIVE' ORDER BY sa.createdAt DESC")
    List<StockAlertJpaEntity> findActiveAlertsByProductId(@Param("productId") Long productId);

    @Query("SELECT sa FROM StockAlertJpaEntity sa WHERE sa.productId = :productId AND sa.status IN ('ACTIVE', 'ACKNOWLEDGED') ORDER BY sa.createdAt DESC")
    List<StockAlertJpaEntity> findActiveOrAcknowledgedAlertsByProductId(@Param("productId") Long productId);
}
