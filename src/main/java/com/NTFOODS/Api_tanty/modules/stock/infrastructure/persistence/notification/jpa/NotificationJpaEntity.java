package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.notification.jpa;

import com.NTFOODS.Api_tanty.modules.stock.domain.notification.entity.Notification;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * NotificationJpaEntity - Entité JPA pour Notification
 */
@Entity
@Table(name = "notifications")
public class NotificationJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Notification.NotificationType type;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private Notification.NotificationChannel channel;
    
    @Column(name = "recipient_id")
    private String recipientId;
    
    @Column(name = "recipient_email")
    private String recipientEmail;
    
    @Column(name = "recipient_phone")
    private String recipientPhone;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "priority", nullable = false)
    private String priority;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;
    
    public NotificationJpaEntity() {}
    
    public NotificationJpaEntity(Notification.NotificationType type, Notification.NotificationChannel channel,
                               String recipientId, String recipientEmail, String recipientPhone,
                               String title, String message, String priority, String metadata) {
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
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Notification.NotificationType getType() { return type; }
    public void setType(Notification.NotificationType type) { this.type = type; }
    
    public Notification.NotificationChannel getChannel() { return channel; }
    public void setChannel(Notification.NotificationChannel channel) { this.channel = channel; }
    
    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }
    
    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
    
    public String getRecipientPhone() { return recipientPhone; }
    public void setRecipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
    
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}
