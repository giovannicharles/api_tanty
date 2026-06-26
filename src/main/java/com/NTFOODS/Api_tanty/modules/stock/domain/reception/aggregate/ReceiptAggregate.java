package com.NTFOODS.Api_tanty.modules.stock.domain.reception.aggregate;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.WarehouseId;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.entity.ReceiptItem;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceiptStatus;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.SourceType;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.events.ReceiptFirstValidatedEvent;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.events.ReceiptSecondValidatedEvent;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject.ReceiptNumber;
import com.NTFOODS.Api_tanty.shared.kernel.event.DomainEvent;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Agrégat racine pour une réception (entrée de marchandises).
 * Gère la double validation et les écarts.
 */
public class ReceiptAggregate {
    private final ReceiptNumber receiptNumber;
    private final SourceType source;
    private final Long sourceId;          // id du PurchaseOrder ou InternalOrder
    private final LocalDate receiptDate;
    private final WarehouseId warehouseId;
    private ReceiptStatus status;
    private UserId firstValidator;
    private LocalDate firstValidatedAt;
    private UserId secondValidator;
    private LocalDate secondValidatedAt;
    private final List<ReceiptItem> items;
    private boolean requiresAuthCode;
    private String rejectionReason;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private ReceiptAggregate(ReceiptNumber receiptNumber, SourceType source, Long sourceId,
                             LocalDate receiptDate, WarehouseId warehouseId, List<ReceiptItem> items) {
        this.receiptNumber = receiptNumber;
        this.source = source;
        this.sourceId = sourceId;
        this.receiptDate = receiptDate;
        this.warehouseId = warehouseId;
        this.items = new ArrayList<>(items);
        this.status = ReceiptStatus.PENDING_FIRST_VALIDATION;
        this.requiresAuthCode = false;
    }

    // Factory
    public static ReceiptAggregate create(ReceiptNumber receiptNumber, SourceType source, Long sourceId,
                                          LocalDate receiptDate, WarehouseId warehouseId,
                                          List<ReceiptItem> items) {
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("Au moins un article requis");
        return new ReceiptAggregate(receiptNumber, source, sourceId, receiptDate, warehouseId, items);
    }

    // Première validation (gestionnaire de stock)
    public void firstValidation(UserId validator, String notes) {
        if (status != ReceiptStatus.PENDING_FIRST_VALIDATION)
            throw new IllegalStateException("Réception non en attente de première validation");
        this.firstValidator = validator;
        this.firstValidatedAt = LocalDate.now();
        this.status = ReceiptStatus.PENDING_SECOND_VALIDATION;
        this.requiresAuthCode = checkIfAuthCodeRequired();
        domainEvents.add(new ReceiptFirstValidatedEvent(this.receiptNumber, validator));
    }

    // Seconde validation (chef prod ou contrôle général)
    public void secondValidation(UserId validator, String authCode) {
        if (status != ReceiptStatus.PENDING_SECOND_VALIDATION)
            throw new IllegalStateException("Réception non en attente de seconde validation");
        if (requiresAuthCode && (authCode == null || authCode.isBlank()))
            throw new IllegalArgumentException("Code d'autorisation requis (écart important)");
        this.secondValidator = validator;
        this.secondValidatedAt = LocalDate.now();
        this.status = ReceiptStatus.VALIDATED;
        domainEvents.add(new ReceiptSecondValidatedEvent(this.receiptNumber, validator));
    }

    // Rejet
    public void reject(String reason, UserId rejectedBy) {
        if (status == ReceiptStatus.VALIDATED || status == ReceiptStatus.REJECTED)
            throw new IllegalStateException("Réception déjà terminée");
        this.status = ReceiptStatus.REJECTED;
        this.rejectionReason = reason;
    }

    // Enregistrer un écart sur une ligne
    public void addDeviation(ProductId productId, Quantity receivedQty, String reason) {
        ReceiptItem item = findItem(productId);
        if (item == null) throw new IllegalArgumentException("Produit non trouvé");
        item.setReceivedQty(receivedQty, reason);
        if (!item.isExactMatch() && Math.abs(item.getDifference().getValue().doubleValue()) > 10.0) {
            this.requiresAuthCode = true;
        }
    }

    private ReceiptItem findItem(ProductId productId) {
        return items.stream().filter(i -> i.getProductId().equals(productId)).findFirst().orElse(null);
    }

    private boolean checkIfAuthCodeRequired() {
        return items.stream().anyMatch(i -> !i.isExactMatch() &&
                Math.abs(i.getDifference().getValue().doubleValue()) > 10.0);
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearEvents() {
        domainEvents.clear();
    }

    // Getters (lecture seule)
    public ReceiptNumber getReceiptNumber() { return receiptNumber; }
    public SourceType getSource() { return source; }
    public Long getSourceId() { return sourceId; }
    public LocalDate getReceiptDate() { return receiptDate; }
    public WarehouseId getWarehouseId() { return warehouseId; }
    public ReceiptStatus getStatus() { return status; }
    public UserId getFirstValidator() { return firstValidator; }
    public LocalDate getFirstValidatedAt() { return firstValidatedAt; }
    public UserId getSecondValidator() { return secondValidator; }
    public LocalDate getSecondValidatedAt() { return secondValidatedAt; }
    public List<ReceiptItem> getItems() { return Collections.unmodifiableList(items); }
    public boolean isRequiresAuthCode() { return requiresAuthCode; }
    public String getRejectionReason() { return rejectionReason; }
}