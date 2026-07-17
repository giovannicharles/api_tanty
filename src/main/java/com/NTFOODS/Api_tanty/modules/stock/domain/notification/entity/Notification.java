package com.NTFOODS.Api_tanty.modules.stock.domain.notification.entity;

import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import java.time.LocalDateTime;

/**
 * Notification - Entité représentant une notification
 * Supporte les notifications in-app, email et SMS
 */
public class Notification {
    
    private Long id;
    private final NotificationType type;
    private final NotificationChannel channel;
    private final UserId recipientId;
    private final String recipientEmail;
    private final String recipientPhone;
    private final String title;
    private final String message;
    private final String priority; // LOW, MEDIUM, HIGH, URGENT
    private final LocalDateTime createdAt;
    private LocalDateTime readAt;
    private LocalDateTime sentAt;
    private String status; // PENDING, SENT, FAILED, READ
    private String metadata; // Données supplémentaires en JSON
    
    public Notification(NotificationType type, NotificationChannel channel, UserId recipientId,
                      String recipientEmail, String recipientPhone, String title, String message, 
                      String priority, String metadata) {
        this.type = type;
        this.channel = channel;
        this.recipientId = recipientId;
        this.recipientEmail = recipientEmail;
        this.recipientPhone = recipientPhone;
        this.title = title;
        this.message = message;
        this.priority = priority;
        this.metadata = metadata;
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public NotificationChannel getChannel() {
        return channel;
    }
    
    public UserId getRecipientId() {
        return recipientId;
    }
    
    public String getRecipientEmail() {
        return recipientEmail;
    }
    
    public String getRecipientPhone() {
        return recipientPhone;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getReadAt() {
        return readAt;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    /**
     * Marque la notification comme envoyée
     */
    public void markAsSent() {
        this.sentAt = LocalDateTime.now();
        this.status = "SENT";
    }
    
    /**
     * Marque la notification comme échouée
     */
    public void markAsFailed() {
        this.status = "FAILED";
    }
    
    /**
     * Marque la notification comme lue
     */
    public void markAsRead() {
        this.readAt = LocalDateTime.now();
        this.status = "READ";
    }
    
    /**
     * NotificationType - Type de notification
     */
    public enum NotificationType {
        STOCK_ALERT,           // Alerte de stock
        DOTATION_REQUEST,      // Demande de dotation
        DOTION_APPROVED,       // Dotation approuvée
        DOTION_REJECTED,       // Dotation rejetée
        RECEPTION_VALIDATION,  // Validation de réception
        STOCK_MOVEMENT,        // Mouvement de stock
        REPORT_GENERATED,      // Rapport généré
        SYSTEM_NOTIFICATION    // Notification système
    }
    
    /**
     * NotificationChannel - Canal de notification
     */
    public enum NotificationChannel {
        IN_APP,   // Notification dans l'application
        EMAIL,     // Email
        SMS        // SMS
    }
}
