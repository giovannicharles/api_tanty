package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.domain.notification.entity.Notification;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.notification.jpa.NotificationJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.notification.repository.NotificationRepository;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * NotificationService - Service pour gérer les notifications
 * Supporte les notifications in-app, email et SMS
 */
@Service
@Transactional
public class NotificationService {
    
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    
    private final NotificationRepository notificationRepository;
    
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    
    /**
     * Crée une notification in-app
     */
    /**
     * Crée une notification in-app (type SYSTEM_NOTIFICATION par défaut)
     */
    public Notification createInAppNotification(UserId recipientId, String title, String message,
                                               String priority, String metadata) {
        return createInAppNotification(Notification.NotificationType.SYSTEM_NOTIFICATION, recipientId, title, message, priority, metadata);
    }

    /**
     * Crée une notification in-app avec un type explicite (ex: RECEPTION_VALIDATION).
     * L'ancienne version forçait toujours SYSTEM_NOTIFICATION, ce qui empêchait le
     * frontend de distinguer/filtrer les notifications par catégorie métier.
     */
    public Notification createInAppNotification(Notification.NotificationType type, UserId recipientId, String title, String message,
                                               String priority, String metadata) {
        NotificationJpaEntity entity = new NotificationJpaEntity(
                type,
                Notification.NotificationChannel.IN_APP,
                recipientId.getMatricule(),
                null,
                null,
                title,
                message,
                priority,
                metadata
        );
        
        NotificationJpaEntity saved = notificationRepository.save(entity);
        log.info("Created in-app notification for user: {}", recipientId.getMatricule());
        
        return mapToDomain(saved);
    }
    
    /**
     * Crée une notification email
     */
    public Notification createEmailNotification(UserId recipientId, String recipientEmail, String title,
                                             String message, String priority, String metadata) {
        NotificationJpaEntity entity = new NotificationJpaEntity(
                Notification.NotificationType.SYSTEM_NOTIFICATION,
                Notification.NotificationChannel.EMAIL,
                recipientId.getMatricule(),
                recipientEmail,
                null,
                title,
                message,
                priority,
                metadata
        );
        
        NotificationJpaEntity saved = notificationRepository.save(entity);
        log.info("Created email notification for user: {}", recipientId.getMatricule());
        
        // Envoyer l'email de manière asynchrone
        sendEmailAsync(saved);
        
        return mapToDomain(saved);
    }
    
    /**
     * Crée une notification SMS
     */
    public Notification createSmsNotification(UserId recipientId, String recipientPhone, String title,
                                            String message, String priority, String metadata) {
        NotificationJpaEntity entity = new NotificationJpaEntity(
                Notification.NotificationType.SYSTEM_NOTIFICATION,
                Notification.NotificationChannel.SMS,
                recipientId.getMatricule(),
                null,
                recipientPhone,
                title,
                message,
                priority,
                metadata
        );
        
        NotificationJpaEntity saved = notificationRepository.save(entity);
        log.info("Created SMS notification for user: {}", recipientId.getMatricule());
        
        // Envoyer le SMS de manière asynchrone
        sendSmsAsync(saved);
        
        return mapToDomain(saved);
    }
    
    /**
     * Crée une notification d'alerte de stock
     */
    public Notification createStockAlertNotification(UserId recipientId, String title, String message,
                                                   String priority, String metadata) {
        NotificationJpaEntity entity = new NotificationJpaEntity(
                Notification.NotificationType.STOCK_ALERT,
                Notification.NotificationChannel.IN_APP,
                recipientId.getMatricule(),
                null,
                null,
                title,
                message,
                priority,
                metadata
        );
        
        NotificationJpaEntity saved = notificationRepository.save(entity);
        log.info("Created stock alert notification for user: {}", recipientId.getMatricule());
        
        return mapToDomain(saved);
    }
    
    /**
     * Crée une notification de demande de dotation
     */
    public Notification createDotationRequestNotification(UserId recipientId, String title, String message,
                                                        String priority, String metadata) {
        NotificationJpaEntity entity = new NotificationJpaEntity(
                Notification.NotificationType.DOTATION_REQUEST,
                Notification.NotificationChannel.IN_APP,
                recipientId.getMatricule(),
                null,
                null,
                title,
                message,
                priority,
                metadata
        );
        
        NotificationJpaEntity saved = notificationRepository.save(entity);
        log.info("Created dotation request notification for user: {}", recipientId.getMatricule());
        
        return mapToDomain(saved);
    }
    
    /**
     * Récupère les notifications d'un utilisateur
     */
    public List<Notification> getUserNotifications(String recipientId) {
        return notificationRepository.findByRecipientId(recipientId).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les notifications en attente d'un utilisateur
     */
    public List<Notification> getPendingNotifications(String recipientId) {
        return notificationRepository.findPendingNotificationsByRecipient(recipientId).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Compte les notifications en attente d'un utilisateur
     */
    public long countPendingNotifications(String recipientId) {
        return notificationRepository.countPendingNotificationsByRecipient(recipientId);
    }
    
    /**
     * Marque une notification comme lue
     */
    public void markAsRead(Long notificationId) {
        NotificationJpaEntity entity = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
        
        entity.setReadAt(LocalDateTime.now());
        entity.setStatus("READ");
        notificationRepository.save(entity);
        
        log.info("Marked notification as read: {}", notificationId);
    }
    
    /**
     * Marque toutes les notifications d'un utilisateur comme lues
     */
    public void markAllAsRead(String recipientId) {
        List<NotificationJpaEntity> pendingNotifications = 
                notificationRepository.findPendingNotificationsByRecipient(recipientId);
        
        for (NotificationJpaEntity entity : pendingNotifications) {
            entity.setReadAt(LocalDateTime.now());
            entity.setStatus("READ");
        }
        
        notificationRepository.saveAll(pendingNotifications);
        log.info("Marked all notifications as read for user: {}", recipientId);
    }
    
    /**
     * Supprime une notification
     */
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
        log.info("Deleted notification: {}", notificationId);
    }
    
    /**
     * Envoie un email de manière asynchrone
     */
    @Async
    protected void sendEmailAsync(NotificationJpaEntity notification) {
        try {
            // Placeholder: Dans une implémentation réelle, on utiliserait un service d'envoi d'emails
            // comme JavaMailSender ou un service externe (SendGrid, AWS SES, etc.)
            log.info("Sending email to: {} - Subject: {}", notification.getRecipientEmail(), notification.getTitle());
            
            // Simuler l'envoi
            Thread.sleep(1000);
            
            notification.setSentAt(LocalDateTime.now());
            notification.setStatus("SENT");
            notificationRepository.save(notification);
            
            log.info("Email sent successfully to: {}", notification.getRecipientEmail());
        } catch (Exception e) {
            log.error("Failed to send email to: {}", notification.getRecipientEmail(), e);
            notification.setStatus("FAILED");
            notificationRepository.save(notification);
        }
    }
    
    /**
     * Envoie un SMS de manière asynchrone
     */
    @Async
    protected void sendSmsAsync(NotificationJpaEntity notification) {
        try {
            // Placeholder: Dans une implémentation réelle, on utiliserait un service d'envoi de SMS
            // comme Twilio, AWS SNS, etc.
            log.info("Sending SMS to: {} - Message: {}", notification.getRecipientPhone(), notification.getTitle());
            
            // Simuler l'envoi
            Thread.sleep(1000);
            
            notification.setSentAt(LocalDateTime.now());
            notification.setStatus("SENT");
            notificationRepository.save(notification);
            
            log.info("SMS sent successfully to: {}", notification.getRecipientPhone());
        } catch (Exception e) {
            log.error("Failed to send SMS to: {}", notification.getRecipientPhone(), e);
            notification.setStatus("FAILED");
            notificationRepository.save(notification);
        }
    }
    
    private Notification mapToDomain(NotificationJpaEntity entity) {
        Notification notification = new Notification(
                entity.getType(),
                entity.getChannel(),
                entity.getRecipientId() != null ? new UserId(entity.getRecipientId()) : null,
                entity.getRecipientEmail(),
                entity.getRecipientPhone(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getPriority(),
                entity.getMetadata()
        );
        notification.setId(entity.getId());
        
        if ("SENT".equals(entity.getStatus()) || "READ".equals(entity.getStatus())) {
            notification.markAsSent();
        }
        
        if ("READ".equals(entity.getStatus())) {
            notification.markAsRead();
        }
        
        if ("FAILED".equals(entity.getStatus())) {
            notification.markAsFailed();
        }
        
        return notification;
    }
}
