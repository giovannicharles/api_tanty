package com.NTFOODS.Api_tanty.modules.stock.application.reception;

import com.NTFOODS.Api_tanty.modules.stock.domain.reception.aggregate.ReceiptAggregate;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceiptStatus;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceptionType;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.repository.ReceiptRepository;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject.ReceiptNumber;
import com.NTFOODS.Api_tanty.shared.kernel.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ReceiptQueryService {

    private final ReceiptRepository receiptRepository;

    public ReceiptQueryService(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    public ReceiptAggregate getByNumber(String receiptNumber) {
        return receiptRepository.findByReceiptNumber(new ReceiptNumber(receiptNumber))
                .orElseThrow(() -> new ResourceNotFoundException("Réception introuvable : " + receiptNumber));
    }

    public List<ReceiptAggregate> search(ReceptionType type, ReceiptStatus status, UUID destinationLocationId,
                                          LocalDate from, LocalDate to) {
        return receiptRepository.findAll(type, status, destinationLocationId, from, to);
    }

    /** Réceptions en attente de première validation, filtrées par type si fourni. */
    public List<ReceiptAggregate> pendingFirstValidation(ReceptionType type) {
        List<ReceiptAggregate> all = receiptRepository.findByStatus(ReceiptStatus.PENDING_FIRST_VALIDATION);
        return type == null ? all : all.stream().filter(r -> r.getReceptionType() == type).toList();
    }

    /** Réceptions en attente de seconde validation, filtrées par type si fourni. */
    public List<ReceiptAggregate> pendingSecondValidation(ReceptionType type) {
        List<ReceiptAggregate> all = receiptRepository.findByStatus(ReceiptStatus.PENDING_SECOND_VALIDATION);
        return type == null ? all : all.stream().filter(r -> r.getReceptionType() == type).toList();
    }
}
