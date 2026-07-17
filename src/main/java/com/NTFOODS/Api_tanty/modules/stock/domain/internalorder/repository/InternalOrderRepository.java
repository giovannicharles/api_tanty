package com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.repository;

import com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.aggregate.InternalOrderAggregate;

import java.util.List;
import java.util.Optional;

/** Repository port pour les commandes internes. */
public interface InternalOrderRepository {

    InternalOrderAggregate save(InternalOrderAggregate aggregate);

    Optional<InternalOrderAggregate> findById(Long id);

    List<InternalOrderAggregate> findAll();

    List<InternalOrderAggregate> findActive();

    List<InternalOrderAggregate> findByStatus(String status);
}
