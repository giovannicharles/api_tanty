package com.NTFOODS.Api_tanty.modules.stock.domain.reception.repository;

import com.NTFOODS.Api_tanty.modules.stock.domain.reception.aggregate.ReceiptAggregate;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceiptStatus;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject.ReceiptNumber;

import java.util.List;
import java.util.Optional;

public interface ReceiptRepository {
    void save(ReceiptAggregate receipt);
    Optional<ReceiptAggregate> findByReceiptNumber(ReceiptNumber number);
    List<ReceiptAggregate> findByStatus(ReceiptStatus status);

}
