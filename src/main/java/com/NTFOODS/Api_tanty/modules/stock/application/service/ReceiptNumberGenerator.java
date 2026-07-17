package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceptionType;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.repository.ReceiptRepository;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject.ReceiptNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * ReceiptNumberGenerator - Génère un numéro de réception lisible et unique.
 * L'ancienne version utilisait Math.random() sans aucune vérification d'unicité
 * contre la base : avec 10 000 combinaisons possibles par an, deux réceptions créées
 * le même jour pouvaient obtenir le même numéro et provoquer une erreur de contrainte
 * unique en base (voire, pire, un écrasement silencieux selon le SGBD).
 * On vérifie désormais explicitement l'unicité et on retente si collision.
 */
@Service
@RequiredArgsConstructor
public class ReceiptNumberGenerator {

    private final ReceiptRepository receiptRepository;
    private static final SecureRandom RANDOM = new SecureRandom();

    public ReceiptNumber generate(ReceptionType type) {
        String prefix = switch (type) {
            case CONSOMMABLE -> "REC-CONS";
            case MATIERE_PREMIERE -> "REC-MP";
            case MATERIEL -> "REC-MAT";
            case PRODUIT_FINI -> "REC-PF";
        };
        String year = String.valueOf(java.time.Year.now().getValue());
        for (int attempt = 0; attempt < 20; attempt++) {
            String sequence = String.format("%04d", RANDOM.nextInt(10000));
            ReceiptNumber candidate = new ReceiptNumber(prefix + "-" + year + "-" + sequence);
            if (!receiptRepository.existsByReceiptNumber(candidate)) {
                return candidate;
            }
        }
        // Filet de sécurité : horodatage nanoseconde, collision quasi impossible
        return new ReceiptNumber(prefix + "-" + year + "-" + System.nanoTime() % 100000);
    }
}
