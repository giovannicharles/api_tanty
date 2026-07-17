package com.NTFOODS.Api_tanty.modules.stock.application.reception.handler;

import com.NTFOODS.Api_tanty.modules.stock.application.reception.ReceptionNotifier;
import com.NTFOODS.Api_tanty.modules.stock.application.reception.command.ValidateReceiptCommand;
import com.NTFOODS.Api_tanty.modules.stock.application.service.StockMovementService;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockMovement;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockMovementType;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.aggregate.ReceiptAggregate;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.entity.ReceiptItem;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceptionType;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.repository.ReceiptRepository;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject.ReceiptNumber;
import com.NTFOODS.Api_tanty.shared.kernel.exception.ForbiddenOperationException;
import com.NTFOODS.Api_tanty.shared.kernel.exception.ResourceNotFoundException;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import com.NTFOODS.Api_tanty.shared.infrastructure.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ValidateReceiptHandler - Traite les 2 étapes de validation d'une réception.
 *
 * Réécrit entièrement. L'ancienne version (ValidateReceiptHandler + ReceiptAggregate
 * générique) ne vérifiait à aucun moment que le rôle de l'utilisateur correspondait
 * au rôle attendu pour l'étape (n'importe quel utilisateur authentifié pouvait valider
 * n'importe quelle réception), et ne déclenchait AUCUNE mise à jour du stock à la
 * validation finale : les quantités reçues n'étaient jamais reflétées en Stock Central.
 */
@Service
@Transactional
public class ValidateReceiptHandler {

    private final ReceiptRepository receiptRepository;
    private final StockMovementService stockMovementService;
    private final ReceptionNotifier notifier;

    public ValidateReceiptHandler(ReceiptRepository receiptRepository, StockMovementService stockMovementService,
                                   ReceptionNotifier notifier) {
        this.receiptRepository = receiptRepository;
        this.stockMovementService = stockMovementService;
        this.notifier = notifier;
    }

    public ReceiptAggregate handleFirstValidation(ValidateReceiptCommand command) {
        ReceiptAggregate receipt = load(command.getReceiptNumber());
        String requiredRole = receipt.getRequiredFirstValidatorRole();
        assertRole(requiredRole);

        UserId validator = new UserId(command.getValidatorMatricule());
        receipt.firstValidation(validator, command.getNotes());
        ReceiptAggregate saved = receiptRepository.save(receipt);

        notifier.notifyRole(saved.getRequiredSecondValidatorRole(),
                "Réception en attente de validation finale",
                "La réception " + saved.getReceiptNumber().getValue() + " a passé la première validation et attend votre validation finale.");

        return saved;
    }

    public ReceiptAggregate handleSecondValidation(ValidateReceiptCommand command) {
        ReceiptAggregate receipt = load(command.getReceiptNumber());
        String requiredRole = receipt.getRequiredSecondValidatorRole();
        assertRole(requiredRole);

        UserId validator = new UserId(command.getValidatorMatricule());
        receipt.secondValidation(validator, command.getNotes(), command.getAuthCode());
        ReceiptAggregate saved = receiptRepository.save(receipt);

        // Entrée effective en Stock Central : un mouvement de stock est créé ET validé
        // immédiatement pour chaque ligne de la réception. C'est ce chaînon qui manquait
        // entièrement dans l'ancienne implémentation.
        StockLocationId destination = new StockLocationId(saved.getDestinationLocationId());
        StockMovementType movementType = toMovementType(saved.getReceptionType());
        for (ReceiptItem item : saved.getItems()) {
            StockMovement movement = stockMovementService.createMovement(
                    movementType,
                    null,
                    destination,
                    item.getProductId().getValue(),
                    item.getProductSku(),
                    item.getPackagingType(),
                    item.getReceivedQty().getValue(),
                    item.getQuantityPerCarton(),
                    validator,
                    saved.getReceiptNumber().getValue(),
                    "Entrée stock suite réception " + saved.getReceiptNumber().getValue()
            );
            stockMovementService.validateMovement(movement.getId(), validator);
        }

        notifier.notifyRole(saved.getRequiredFirstValidatorRole(),
                "Réception validée",
                "La réception " + saved.getReceiptNumber().getValue() + " a été validée et les quantités sont entrées en stock.");

        return saved;
    }

    private StockMovementType toMovementType(ReceptionType type) {
        return switch (type) {
            case CONSOMMABLE -> StockMovementType.RECEPTION_CONSOMMABLE;
            case MATIERE_PREMIERE -> StockMovementType.RECEPTION_RAW_MATERIAL;
            case MATERIEL -> StockMovementType.RECEPTION_MATERIEL;
            case PRODUIT_FINI -> StockMovementType.RECEPTION_PRODUCTION;
        };
    }

    private ReceiptAggregate load(String receiptNumber) {
        return receiptRepository.findByReceiptNumber(new ReceiptNumber(receiptNumber))
                .orElseThrow(() -> new ResourceNotFoundException("Réception introuvable : " + receiptNumber));
    }

    private void assertRole(String requiredRole) {
        if (!SecurityUtils.hasRoleOrOverride(requiredRole)) {
            throw new ForbiddenOperationException(
                    "Cette étape de validation requiert le rôle " + requiredRole + ".");
        }
    }
}
