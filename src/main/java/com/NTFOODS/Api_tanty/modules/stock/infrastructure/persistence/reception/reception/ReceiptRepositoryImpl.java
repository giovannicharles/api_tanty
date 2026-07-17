package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.reception.reception;

import com.NTFOODS.Api_tanty.modules.stock.domain.reception.aggregate.ReceiptAggregate;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceiptStatus;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceptionType;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.repository.ReceiptRepository;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject.ReceiptNumber;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.reception.jpa.ReceiptJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.reception.jpa.ReceiptJpaRepository;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.reception.jpa.ReceiptSpecifications;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.reception.mapper.ReceiptMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ReceiptRepositoryImpl - Implémentation réelle de la persistance des réceptions.
 * L'ancienne version était un stub complet : save() ne faisait rien et
 * findByReceiptNumber() retournait toujours Optional.empty(). Aucune réception
 * n'était donc jamais réellement enregistrée en base, quel que soit ce que
 * l'utilisateur saisissait côté frontend.
 */
@Repository
@RequiredArgsConstructor
public class ReceiptRepositoryImpl implements ReceiptRepository {

    private final ReceiptJpaRepository jpaRepository;

    @Override
    public ReceiptAggregate save(ReceiptAggregate receipt) {
        ReceiptJpaEntity entity = ReceiptMapper.toEntity(receipt);
        ReceiptJpaEntity saved = jpaRepository.save(entity);
        return ReceiptMapper.toDomain(saved);
    }

    @Override
    public Optional<ReceiptAggregate> findByReceiptNumber(ReceiptNumber receiptNumber) {
        return jpaRepository.findByReceiptNumber(receiptNumber.getValue()).map(ReceiptMapper::toDomain);
    }

    @Override
    public Optional<ReceiptAggregate> findById(Long id) {
        return jpaRepository.findById(id).map(ReceiptMapper::toDomain);
    }

    @Override
    public List<ReceiptAggregate> findByStatus(ReceiptStatus status) {
        return jpaRepository.findByStatus(status.name()).stream().map(ReceiptMapper::toDomain).toList();
    }

    @Override
    public List<ReceiptAggregate> findByStatusAndType(ReceiptStatus status, ReceptionType type) {
        return jpaRepository.findByStatusAndReceptionType(status.name(), type.name())
                .stream().map(ReceiptMapper::toDomain).toList();
    }

    @Override
    public List<ReceiptAggregate> findAll(ReceptionType type, ReceiptStatus status, UUID destinationLocationId,
                                           LocalDate from, LocalDate to) {
        // Le filtre par emplacement est appliqué en mémoire : le volume de réceptions
        // reste modeste (usage ERP interne) et cela évite une jointure supplémentaire
        // pour un filtre optionnel rarement combiné aux autres.
        var spec = ReceiptSpecifications.withFilters(
                type != null ? type.name() : null,
                status != null ? status.name() : null,
                from != null ? from.atStartOfDay() : null,
                to != null ? to.atTime(23, 59, 59) : null
        );
        return jpaRepository.findAll(spec).stream()
                .filter(e -> destinationLocationId == null || destinationLocationId.equals(e.getDestinationLocationId()))
                .map(ReceiptMapper::toDomain)
                .toList();
    }

    @Override
    public List<ReceiptAggregate> findByDestinationLocation(UUID destinationLocationId) {
        return jpaRepository.findByDestinationLocationId(destinationLocationId)
                .stream().map(ReceiptMapper::toDomain).toList();
    }

    @Override
    public boolean existsByReceiptNumber(ReceiptNumber receiptNumber) {
        return jpaRepository.existsByReceiptNumber(receiptNumber.getValue());
    }
}
