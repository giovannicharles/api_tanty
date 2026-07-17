package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.reception.jpa;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

/**
 * ReceiptSpecifications - Construit une requête dynamique pour ReceiptJpaEntity en
 * n'ajoutant un prédicat que pour les filtres réellement fournis. Remplace le pattern
 * "(:param IS NULL OR champ = :param)" qui échouait sous PostgreSQL avec
 * "could not determine data type of parameter" dès qu'un des paramètres optionnels
 * était null.
 */
public final class ReceiptSpecifications {

    private ReceiptSpecifications() {}

    public static Specification<ReceiptJpaEntity> withFilters(String receptionType, String status,
                                                                LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (receptionType != null && !receptionType.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("receptionType"), receptionType));
            }
            if (status != null && !status.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            if (from != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("receiptDate"), from));
            }
            if (to != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("receiptDate"), to));
            }
            if (query != null) {
                query.orderBy(cb.desc(root.get("receiptDate")));
            }
            return predicate;
        };
    }
}
