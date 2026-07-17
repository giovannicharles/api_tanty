package com.NTFOODS.Api_tanty.modules.stock.domain.reception.repository;

import com.NTFOODS.Api_tanty.modules.stock.domain.reception.aggregate.ReceiptAggregate;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceiptStatus;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceptionType;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject.ReceiptNumber;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReceiptRepository {

    ReceiptAggregate save(ReceiptAggregate receipt);

    Optional<ReceiptAggregate> findByReceiptNumber(ReceiptNumber receiptNumber);

    Optional<ReceiptAggregate> findById(Long id);

    List<ReceiptAggregate> findByStatus(ReceiptStatus status);

    List<ReceiptAggregate> findByStatusAndType(ReceiptStatus status, ReceptionType type);

    List<ReceiptAggregate> findAll(ReceptionType type, ReceiptStatus status, UUID destinationLocationId,
                                    LocalDate from, LocalDate to);

    List<ReceiptAggregate> findByDestinationLocation(UUID destinationLocationId);

    boolean existsByReceiptNumber(ReceiptNumber receiptNumber);
}
