package com.NTFOODS.Api_tanty.modules.stock.infrastructure.web;

import com.NTFOODS.Api_tanty.modules.stock.application.service.NotificationService;
import com.NTFOODS.Api_tanty.modules.stock.domain.notification.entity.Notification;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/in-app")
    public ResponseEntity<Notification> createInAppNotification(
            @RequestParam UUID recipientId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam String priority,
            @RequestParam(required = false) String metadata) {
        
        UserId userId = new UserId(recipientId.toString());
        Notification notification = notificationService.createInAppNotification(
                userId, title, message, priority, metadata);
        
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/email")
    public ResponseEntity<Notification> createEmailNotification(
            @RequestParam UUID recipientId,
            @RequestParam String recipientEmail,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam String priority,
            @RequestParam(required = false) String metadata) {
        
        UserId userId = new UserId(recipientId.toString());
        Notification notification = notificationService.createEmailNotification(
                userId, recipientEmail, title, message, priority, metadata);
        
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/sms")
    public ResponseEntity<Notification> createSmsNotification(
            @RequestParam UUID recipientId,
            @RequestParam String recipientPhone,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam String priority,
            @RequestParam(required = false) String metadata) {
        
        UserId userId = new UserId(recipientId.toString());
        Notification notification = notificationService.createSmsNotification(
                userId, recipientPhone, title, message, priority, metadata);
        
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/stock-alert")
    public ResponseEntity<Notification> createStockAlertNotification(
            @RequestParam UUID recipientId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam String priority,
            @RequestParam(required = false) String metadata) {
        
        UserId userId = new UserId(recipientId.toString());
        Notification notification = notificationService.createStockAlertNotification(
                userId, title, message, priority, metadata);
        
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/dotation-request")
    public ResponseEntity<Notification> createDotationRequestNotification(
            @RequestParam UUID recipientId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam String priority,
            @RequestParam(required = false) String metadata) {
        
        UserId userId = new UserId(recipientId.toString());
        Notification notification = notificationService.createDotationRequestNotification(
                userId, title, message, priority, metadata);
        
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/user/{recipientId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable String recipientId) {
        List<Notification> notifications = notificationService.getUserNotifications(recipientId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{recipientId}/pending")
    public ResponseEntity<List<Notification>> getPendingNotifications(@PathVariable String recipientId) {
        List<Notification> notifications = notificationService.getPendingNotifications(recipientId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{recipientId}/pending/count")
    public ResponseEntity<Long> countPendingNotifications(@PathVariable String recipientId) {
        long count = notificationService.countPendingNotifications(recipientId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/{id}/mark-read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/{recipientId}/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(@PathVariable String recipientId) {
        notificationService.markAllAsRead(recipientId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
