package com.NTFOODS.Api_tanty.modules.stock.application.reception.handler;

import com.NTFOODS.Api_tanty.modules.stock.application.reception.command.ValidateReceiptCommand;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.aggregate.ReceiptAggregate;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceiptStatus;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.repository.ReceiptRepository;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject.ReceiptNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ValidateReceiptHandler - Handler pour la validation de réceptions
 * Gère la première et la seconde validation des réceptions
 */
@Component
@RequiredArgsConstructor
public class ValidateReceiptHandler {

    @Autowired
    private ReceiptRepository receiptRepository;

    /**
     * Gère la commande de validation de réception
     * @param cmd Commande de validation de réception
     */

    public void handle(ValidateReceiptCommand cmd) {
        // Récupérer la réception
        ReceiptAggregate receipt = receiptRepository.findByReceiptNumber(cmd.receiptNumber())
                .orElseThrow(() -> new IllegalArgumentException("Réception non trouvée"));

        // Vérifier que le statut actuel correspond
        if (receipt.getStatus() != cmd.currentStatus()) {
            throw new IllegalStateException("Statut de la réception invalide");
        }

        // Effectuer la validation appropriée
        if (cmd.currentStatus() == ReceiptStatus.PENDING_FIRST_VALIDATION) {
            receipt.firstValidation(cmd.validator(), cmd.notes());
        } else if (cmd.currentStatus() == ReceiptStatus.PENDING_SECOND_VALIDATION) {
            receipt.secondValidation(cmd.validator(), cmd.authCode());
        } else {
            throw new IllegalStateException("Réception non en attente de validation");
        }

        // Sauvegarder les modifications
        receiptRepository.save(receipt);
    }
}
