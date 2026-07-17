package com.NTFOODS.Api_tanty.modules.stock.domain.reception.aggregate;

import java.util.UUID;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.entity.ReceiptItem;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceiptStatus;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceptionType;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.events.ReceiptCreatedEvent;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.events.ReceiptFirstValidatedEvent;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.events.ReceiptRejectedEvent;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.events.ReceiptSecondValidatedEvent;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject.ReceiptNumber;
import com.NTFOODS.Api_tanty.shared.kernel.event.DomainEvent;
import com.NTFOODS.Api_tanty.shared.kernel.exception.InvalidStateException;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ReceiptAggregate - Agrégat racine d'une réception (entrée de marchandises).
 *
 * Unifie les 3 circuits de double validation du cahier des charges NT Foods (§3) :
 *  - CONSOMMABLE      : Gestionnaire de stock -> Contrôleur Général
 *  - MATIERE_PREMIERE : Gestionnaire de stock -> Comptable
 *  - MATERIEL         : Gestionnaire de stock -> Contrôleur Général
 *
 * Cette classe remplace 3 implémentations concurrentes trouvées dans le code repris
 * (ReceiptAggregate générique sans distinction de rôle, ReceptionValidation sans lignes
 * de produits, ProductionReceptionController ad hoc sans validation). Le rôle exact
 * attendu à chaque étape est exposé via getRequiredFirstValidatorRole()/
 * getRequiredSecondValidatorRole() et vérifié par la couche application
 * (ReceptionRolePolicy), le domaine restant volontairement indépendant du module Users.
 *
 * Principe métier : selon le cahier des charges actuel, une réception peut être
 * rejetée (ex: marchandise non conforme, fournisseur refusé) - le rejet est motivé
 * et tracé (rejectedBy + reason). Ce point a évolué par rapport à une règle plus
 * ancienne "aucune réception n'est jamais refusée" ; à confirmer avec Giovanni si les
 * deux règles doivent coexister selon le type de réception.
 */
public class ReceiptAggregate {
    private Long id;
    private final ReceiptNumber receiptNumber;
    private final ReceptionType receptionType;
    private final String sourceLabel;      // Nom fournisseur, ou référence bon de commande/production interne
    private final Long sourceId;           // id du PurchaseOrder / InternalOrder / ProductionOrder (nullable)
    private final LocalDateTime receiptDate;
    private final UUID destinationLocationId;
    private final UserId createdBy;
    private ReceiptStatus status;
    private UserId firstValidator;
    private LocalDateTime firstValidatedAt;
    private String firstValidationNotes;
    private UserId secondValidator;
    private LocalDateTime secondValidatedAt;
    private String secondValidationNotes;
    private final List<ReceiptItem> items;
    private boolean requiresAuthCode;
    private String rejectionReason;
    private UserId rejectedBy;
    private LocalDateTime rejectedAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private static final BigDecimal DEVIATION_THRESHOLD_PERCENT = BigDecimal.TEN;

    private ReceiptAggregate(ReceiptNumber receiptNumber, ReceptionType receptionType, String sourceLabel,
                              Long sourceId, LocalDateTime receiptDate, UUID destinationLocationId,
                              UserId createdBy, List<ReceiptItem> items) {
        this.receiptNumber = receiptNumber;
        this.receptionType = receptionType;
        this.sourceLabel = sourceLabel;
        this.sourceId = sourceId;
        this.receiptDate = receiptDate;
        this.destinationLocationId = destinationLocationId;
        this.createdBy = createdBy;
        this.items = new ArrayList<>(items);
        this.status = ReceiptStatus.PENDING_FIRST_VALIDATION;
        this.requiresAuthCode = false;
    }

    /** Création d'une nouvelle réception (déclenche ReceiptCreatedEvent). */
    public static ReceiptAggregate create(ReceiptNumber receiptNumber, ReceptionType receptionType,
                                           String sourceLabel, Long sourceId, LocalDateTime receiptDate,
                                           UUID destinationLocationId, UserId createdBy, List<ReceiptItem> items) {
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("Au moins un article est requis pour créer une réception");
        if (destinationLocationId == null)
            throw new IllegalArgumentException("L'emplacement de stock de destination est requis");
        ReceiptAggregate aggregate = new ReceiptAggregate(receiptNumber, receptionType, sourceLabel, sourceId,
                receiptDate, destinationLocationId, createdBy, items);
        aggregate.domainEvents.add(new ReceiptCreatedEvent(receiptNumber, receptionType, createdBy));
        return aggregate;
    }

    /** Ré-hydratation depuis la persistance : ne rejoue AUCUNE règle métier ni horodatage. */
    public static ReceiptAggregate hydrate(Long id, ReceiptNumber receiptNumber, ReceptionType receptionType,
                                            String sourceLabel, Long sourceId, LocalDateTime receiptDate,
                                            UUID destinationLocationId, UserId createdBy, List<ReceiptItem> items,
                                            ReceiptStatus status, UserId firstValidator, LocalDateTime firstValidatedAt,
                                            String firstValidationNotes, UserId secondValidator,
                                            LocalDateTime secondValidatedAt, String secondValidationNotes,
                                            boolean requiresAuthCode, String rejectionReason, UserId rejectedBy,
                                            LocalDateTime rejectedAt) {
        ReceiptAggregate a = new ReceiptAggregate(receiptNumber, receptionType, sourceLabel, sourceId,
                receiptDate, destinationLocationId, createdBy, items);
        a.id = id;
        a.status = status;
        a.firstValidator = firstValidator;
        a.firstValidatedAt = firstValidatedAt;
        a.firstValidationNotes = firstValidationNotes;
        a.secondValidator = secondValidator;
        a.secondValidatedAt = secondValidatedAt;
        a.secondValidationNotes = secondValidationNotes;
        a.requiresAuthCode = requiresAuthCode;
        a.rejectionReason = rejectionReason;
        a.rejectedBy = rejectedBy;
        a.rejectedAt = rejectedAt;
        return a;
    }

    /** Rôle métier requis pour la 1ère validation, selon le type de réception. */
    public String getRequiredFirstValidatorRole() {
        return switch (receptionType) {
            case CONSOMMABLE, MATIERE_PREMIERE, MATERIEL, PRODUIT_FINI -> "ROLE_STOCK";
        };
    }

    /** Rôle métier requis pour la 2nde validation, selon le type de réception. */
    public String getRequiredSecondValidatorRole() {
        return switch (receptionType) {
            case CONSOMMABLE, MATERIEL -> "ROLE_VALIDATEUR";   // Contrôleur Général
            case MATIERE_PREMIERE -> "ROLE_FINANCE"; // Comptable
            case PRODUIT_FINI -> "ROLE_PRODUCTION"; // Chef de production
        };
    }

    /** Enregistre les quantités réellement reçues et contrôlées pour chaque ligne, avant la 1ère validation. */
    public void recordReceivedQuantities(java.util.Map<ProductId, Quantity> receivedByProduct,
                                          java.util.Map<ProductId, String> reasonsByProduct) {
        if (status != ReceiptStatus.PENDING_FIRST_VALIDATION)
            throw new InvalidStateException("Les quantités reçues ne peuvent être saisies qu'avant la première validation");
        for (ReceiptItem item : items) {
            Quantity received = receivedByProduct.get(item.getProductId());
            if (received == null) continue;
            item.recordReceivedQuantity(received, reasonsByProduct.get(item.getProductId()));
        }
        this.requiresAuthCode = checkIfAuthCodeRequired();
    }

    /** Première validation (rôle dépendant du type - cf. getRequiredFirstValidatorRole). */
    public void firstValidation(UserId validator, String notes) {
        if (status != ReceiptStatus.PENDING_FIRST_VALIDATION)
            throw new InvalidStateException("Cette réception n'est pas en attente de première validation (statut actuel : " + status + ")");
        if (items.stream().allMatch(i -> i.getReceivedQty().getValue().compareTo(BigDecimal.ZERO) == 0))
            throw new IllegalArgumentException("Les quantités reçues doivent être saisies avant la première validation");
        this.firstValidator = validator;
        this.firstValidatedAt = LocalDateTime.now();
        this.firstValidationNotes = notes;
        this.status = ReceiptStatus.PENDING_SECOND_VALIDATION;
        this.requiresAuthCode = checkIfAuthCodeRequired();
        domainEvents.add(new ReceiptFirstValidatedEvent(this.receiptNumber, validator));
    }

    /** Seconde validation : finalise la réception et autorise l'entrée en stock central. */
    public void secondValidation(UserId validator, String notes, String authCode) {
        if (status != ReceiptStatus.PENDING_SECOND_VALIDATION)
            throw new InvalidStateException("Cette réception n'est pas en attente de seconde validation (statut actuel : " + status + ")");
        if (requiresAuthCode && (authCode == null || authCode.isBlank()))
            throw new IllegalArgumentException("Code d'autorisation de la Direction Générale requis (écart supérieur à " + DEVIATION_THRESHOLD_PERCENT + "%)");
        this.secondValidator = validator;
        this.secondValidatedAt = LocalDateTime.now();
        this.secondValidationNotes = notes;
        this.status = ReceiptStatus.VALIDATED;
        domainEvents.add(new ReceiptSecondValidatedEvent(this.receiptNumber, validator));
    }

    /** Rejet de la réception (motivé et tracé), possible tant qu'elle n'est pas déjà finalisée. */
    public void reject(UserId rejectedBy, String reason) {
        if (status == ReceiptStatus.VALIDATED || status == ReceiptStatus.REJECTED)
            throw new InvalidStateException("Cette réception est déjà finalisée (statut : " + status + ") et ne peut plus être rejetée");
        if (reason == null || reason.isBlank())
            throw new IllegalArgumentException("Un motif de rejet est requis");
        this.status = ReceiptStatus.REJECTED;
        this.rejectionReason = reason;
        this.rejectedBy = rejectedBy;
        this.rejectedAt = LocalDateTime.now();
        domainEvents.add(new ReceiptRejectedEvent(this.receiptNumber, rejectedBy, reason));
    }

    private boolean checkIfAuthCodeRequired() {
        return items.stream().anyMatch(i -> !i.isExactMatch()
                && i.getDeviationPercent().compareTo(DEVIATION_THRESHOLD_PERCENT) > 0);
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearEvents() {
        domainEvents.clear();
    }

    // Getters (lecture seule)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ReceiptNumber getReceiptNumber() { return receiptNumber; }
    public ReceptionType getReceptionType() { return receptionType; }
    public String getSourceLabel() { return sourceLabel; }
    public Long getSourceId() { return sourceId; }
    public LocalDateTime getReceiptDate() { return receiptDate; }
    public UUID getDestinationLocationId() { return destinationLocationId; }
    public UserId getCreatedBy() { return createdBy; }
    public ReceiptStatus getStatus() { return status; }
    public UserId getFirstValidator() { return firstValidator; }
    public LocalDateTime getFirstValidatedAt() { return firstValidatedAt; }
    public String getFirstValidationNotes() { return firstValidationNotes; }
    public UserId getSecondValidator() { return secondValidator; }
    public LocalDateTime getSecondValidatedAt() { return secondValidatedAt; }
    public String getSecondValidationNotes() { return secondValidationNotes; }
    public List<ReceiptItem> getItems() { return Collections.unmodifiableList(items); }
    public boolean isRequiresAuthCode() { return requiresAuthCode; }
    public String getRejectionReason() { return rejectionReason; }
    public UserId getRejectedBy() { return rejectedBy; }
    public LocalDateTime getRejectedAt() { return rejectedAt; }
}
