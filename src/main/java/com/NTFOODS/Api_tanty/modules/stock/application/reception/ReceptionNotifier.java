package com.NTFOODS.Api_tanty.modules.stock.application.reception;

import com.NTFOODS.Api_tanty.modules.stock.application.service.NotificationService;
import com.NTFOODS.Api_tanty.modules.users.domain.aggregate.UserAggregate;
import com.NTFOODS.Api_tanty.modules.users.domain.enums.UserRole;
import com.NTFOODS.Api_tanty.modules.users.domain.repository.UserRepository;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.springframework.stereotype.Component;

/**
 * ReceptionNotifier - Notifie tous les utilisateurs d'un rôle donné (ex: tous les
 * comptables) lorsqu'une réception attend leur validation. Auparavant, le service de
 * réception dédié (ReceptionValidationService) ciblait un UserId précis, mais aucune
 * notion de "rôle en attente" n'existait : impossible de notifier "le Comptable" sans
 * savoir lequel. On diffuse donc désormais à tous les porteurs actifs du rôle requis.
 */
@Component
public class ReceptionNotifier {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ReceptionNotifier(UserRepository userRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public void notifyRole(String roleCode, String title, String message) {
        UserRole role;
        try {
            role = UserRole.valueOf(roleCode);
        } catch (IllegalArgumentException e) {
            return;
        }
        for (UserAggregate user : userRepository.findByRole(role)) {
            notificationService.createInAppNotification(
                    com.NTFOODS.Api_tanty.modules.stock.domain.notification.entity.Notification.NotificationType.RECEPTION_VALIDATION,
                    new UserId(user.getMatricule().value()),
                    title,
                    message,
                    "HIGH",
                    null
            );
        }
    }
}
