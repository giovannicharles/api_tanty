package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.notification.repository;

import com.NTFOODS.Api_tanty.modules.stock.domain.notification.entity.Notification;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.notification.jpa.NotificationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * NotificationRepository - Repository pour NotificationJpaEntity
 */
@Repository
public interface NotificationRepository extends JpaRepository<NotificationJpaEntity, Long> {
    
    List<NotificationJpaEntity> findByRecipientId(String recipientId);
    
    List<NotificationJpaEntity> findByStatus(String status);
    
    List<NotificationJpaEntity> findByType(Notification.NotificationType type);
    
    List<NotificationJpaEntity> findByChannel(Notification.NotificationChannel channel);
    
    @Query("SELECT n FROM NotificationJpaEntity n WHERE n.recipientId = :recipientId AND n.status = 'PENDING' ORDER BY n.createdAt DESC")
    List<NotificationJpaEntity> findPendingNotificationsByRecipient(@Param("recipientId") String recipientId);
    
    @Query("SELECT n FROM NotificationJpaEntity n WHERE n.recipientId = :recipientId AND n.createdAt BETWEEN :startDate AND :endDate ORDER BY n.createdAt DESC")
    List<NotificationJpaEntity> findByRecipientAndDateRange(@Param("recipientId") String recipientId,
                                                           @Param("startDate") LocalDateTime startDate,
                                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(n) FROM NotificationJpaEntity n WHERE n.recipientId = :recipientId AND n.status = 'PENDING'")
    long countPendingNotificationsByRecipient(@Param("recipientId") String recipientId);
    
    @Query("SELECT n FROM NotificationJpaEntity n WHERE n.priority = 'URGENT' AND n.status = 'PENDING'")
    List<NotificationJpaEntity> findUrgentPendingNotifications();
}
