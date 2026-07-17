package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.reception.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ReceiptJpaRepository.
 * L'ancienne méthode search() utilisait le pattern JPQL "(:param IS NULL OR champ = :param)"
 * pour des filtres optionnels. PostgreSQL (contrairement à H2/MySQL) ne peut pas déterminer
 * le type d'un paramètre qui n'apparaît que dans la branche "IS NULL" d'un OR, ce qui provoque
 * "ERROR: could not determine data type of parameter $5" au premier appel avec un paramètre
 * null. On passe donc à JpaSpecificationExecutor : chaque filtre n'ajoute un prédicat que s'il
 * est réellement fourni, sans jamais passer de paramètre "typé de façon ambiguë" à Postgres.
 */
public interface ReceiptJpaRepository extends JpaRepository<ReceiptJpaEntity, Long>,
        JpaSpecificationExecutor<ReceiptJpaEntity> {
    Optional<ReceiptJpaEntity> findByReceiptNumber(String receiptNumber);
    boolean existsByReceiptNumber(String receiptNumber);
    List<ReceiptJpaEntity> findByStatusIn(List<String> statuses);
    List<ReceiptJpaEntity> findByStatus(String status);
    List<ReceiptJpaEntity> findByStatusAndReceptionType(String status, String receptionType);
    List<ReceiptJpaEntity> findByDestinationLocationId(UUID destinationLocationId);
}
