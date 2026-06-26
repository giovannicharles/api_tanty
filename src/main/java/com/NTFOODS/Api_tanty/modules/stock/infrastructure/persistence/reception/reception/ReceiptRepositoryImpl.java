package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.reception.reception;

import com.NTFOODS.Api_tanty.modules.stock.domain.reception.aggregate.ReceiptAggregate;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceiptStatus;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.repository.ReceiptRepository;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject.ReceiptNumber;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.reception.jpa.ReceiptJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public class ReceiptRepositoryImpl implements ReceiptRepository {
  private final ReceiptJpaRepository receiptJpaRepository;

  public ReceiptRepositoryImpl(ReceiptJpaRepository receiptJpaRepository) {
    this.receiptJpaRepository = receiptJpaRepository;
  }

  @Override
  public void save(ReceiptAggregate receipt) {

  }

  @Override
  public Optional<ReceiptAggregate> findByReceiptNumber(ReceiptNumber number) {
    return Optional.empty();
  }

  @Override
  public List<ReceiptAggregate> findByStatus(ReceiptStatus status) {
    return List.of();
  }
}
