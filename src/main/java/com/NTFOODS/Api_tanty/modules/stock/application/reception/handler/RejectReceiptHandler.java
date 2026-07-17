package com.NTFOODS.Api_tanty.modules.stock.application.reception.handler;

import com.NTFOODS.Api_tanty.modules.stock.application.reception.ReceptionNotifier;
import com.NTFOODS.Api_tanty.modules.stock.application.reception.command.RejectReceiptCommand;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.aggregate.ReceiptAggregate;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.repository.ReceiptRepository;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject.ReceiptNumber;
import com.NTFOODS.Api_tanty.shared.kernel.exception.ResourceNotFoundException;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RejectReceiptHandler - Rejet motivé d'une réception, à n'importe quelle étape
 * avant la validation finale. N'existait dans aucune des 3 implémentations reprises.
 */
@Service
@Transactional
public class RejectReceiptHandler {

    private final ReceiptRepository receiptRepository;
    private final ReceptionNotifier notifier;

    public RejectReceiptHandler(ReceiptRepository receiptRepository, ReceptionNotifier notifier) {
        this.receiptRepository = receiptRepository;
        this.notifier = notifier;
    }

    public ReceiptAggregate handle(RejectReceiptCommand command) {
        ReceiptAggregate receipt = receiptRepository.findByReceiptNumber(new ReceiptNumber(command.getReceiptNumber()))
                .orElseThrow(() -> new ResourceNotFoundException("Réception introuvable : " + command.getReceiptNumber()));

        receipt.reject(new UserId(command.getRejectedByMatricule()), command.getReason());
        ReceiptAggregate saved = receiptRepository.save(receipt);

        if (saved.getCreatedBy() != null) {
            notifier.notifyRole(saved.getRequiredFirstValidatorRole(),
                    "Réception rejetée",
                    "La réception " + saved.getReceiptNumber().getValue() + " a été rejetée : " + saved.getRejectionReason());
        }
        return saved;
    }
}
