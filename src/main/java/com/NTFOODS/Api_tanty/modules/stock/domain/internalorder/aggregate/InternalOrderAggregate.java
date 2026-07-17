package com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.aggregate;

import com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.entity.InternalOrderItem;
import com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.enums.InternalOrderStatus;
import com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.valueobject.InternalOrderNumber;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Commande interne du stock à la production. */
public class InternalOrderAggregate {
    private final InternalOrderNumber orderNumber;
    private final LocalDate orderDate;
    private InternalOrderStatus status;
    private final UserId requestedBy;       // gestionnaire de stock
    private final String requestedByName;
    private UserId approvedBy;              // chef production
    private String approvedByName;
    private LocalDateTime approvedAt;
    private UserId cancelledBy;
    private String cancelledReason;
    private LocalDateTime cancelledAt;
    private final LocalDateTime createdAt;
    private final String notes;
    private final List<InternalOrderItem> items;

    private InternalOrderAggregate(InternalOrderNumber orderNumber, LocalDate orderDate,
                                   List<InternalOrderItem> items, UserId requestedBy,
                                   String requestedByName, String notes) {
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.items = new ArrayList<>(items);
        this.requestedBy = requestedBy;
        this.requestedByName = requestedByName;
        this.notes = notes;
        this.status = InternalOrderStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
    }

    /** Factory: créer une nouvelle commande interne. */
    public static InternalOrderAggregate create(InternalOrderNumber orderNumber, LocalDate orderDate,
                                                List<InternalOrderItem> items, UserId requestedBy,
                                                String requestedByName, String notes) {
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("Au moins un article requis");
        return new InternalOrderAggregate(orderNumber, orderDate, items, requestedBy, requestedByName, notes);
    }

    /** Reconstituer un aggregate depuis la persistance (sans validation d'état). */
    public static InternalOrderAggregate reconstitute(InternalOrderNumber orderNumber, LocalDate orderDate,
                                                      InternalOrderStatus status, UserId requestedBy,
                                                      String requestedByName, UserId approvedBy,
                                                      String approvedByName, LocalDateTime approvedAt,
                                                      UserId cancelledBy, String cancelledReason,
                                                      LocalDateTime cancelledAt, LocalDateTime createdAt,
                                                      String notes, List<InternalOrderItem> items) {
        InternalOrderAggregate agg = new InternalOrderAggregate(orderNumber, orderDate, items, requestedBy, requestedByName, notes);
        agg.status = status;
        agg.approvedBy = approvedBy;
        agg.approvedByName = approvedByName;
        agg.approvedAt = approvedAt;
        agg.cancelledBy = cancelledBy;
        agg.cancelledReason = cancelledReason;
        agg.cancelledAt = cancelledAt;
        // createdAt is set in constructor to now(); for reconstitution we accept this approximation
        return agg;
    }

    /** Le Chef de Production approuve la commande. */
    public void approve(UserId approver, String approverName) {
        if (status != InternalOrderStatus.DRAFT)
            throw new IllegalStateException("Seule une commande DRAFT peut être approuvée (statut actuel: " + status + ")");
        this.approvedBy = approver;
        this.approvedByName = approverName;
        this.approvedAt = LocalDateTime.now();
        this.status = InternalOrderStatus.APPROVED;
    }

    /** Annulation de la commande. */
    public void cancel(UserId cancelledBy, String reason) {
        if (status == InternalOrderStatus.DELIVERED || status == InternalOrderStatus.CANCELLED)
            throw new IllegalStateException("Cette commande ne peut plus être annulée (statut: " + status + ")");
        this.cancelledBy = cancelledBy;
        this.cancelledReason = reason;
        this.cancelledAt = LocalDateTime.now();
        this.status = InternalOrderStatus.CANCELLED;
    }

    /** Enregistrer une livraison (partielle ou complète). */
    public void registerDelivery(ProductId productId, Quantity qty) {
        if (status != InternalOrderStatus.APPROVED && status != InternalOrderStatus.PARTIALLY_DELIVERED)
            throw new IllegalStateException("Seules les commandes APPROVED ou PARTIALLY_DELIVERED peuvent recevoir des livraisons (statut: " + status + ")");
        InternalOrderItem item = findItem(productId);
        if (item == null) throw new IllegalArgumentException("Produit " + productId.getValue() + " non trouvé dans la commande");
        item.addDeliveredQty(qty);
        if (isFullyDelivered()) this.status = InternalOrderStatus.DELIVERED;
        else this.status = InternalOrderStatus.PARTIALLY_DELIVERED;
    }

    private boolean isFullyDelivered() {
        return items.stream().allMatch(InternalOrderItem::isFullyDelivered);
    }

    private InternalOrderItem findItem(ProductId productId) {
        return items.stream().filter(i -> i.getProductId().equals(productId)).findFirst().orElse(null);
    }

    // ── Getters ──────────────────────────────────────────────
    public InternalOrderNumber getOrderNumber() { return orderNumber; }
    public LocalDate getOrderDate() { return orderDate; }
    public InternalOrderStatus getStatus() { return status; }
    public UserId getRequestedBy() { return requestedBy; }
    public String getRequestedByName() { return requestedByName; }
    public UserId getApprovedBy() { return approvedBy; }
    public String getApprovedByName() { return approvedByName; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public UserId getCancelledBy() { return cancelledBy; }
    public String getCancelledReason() { return cancelledReason; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getNotes() { return notes; }
    public List<InternalOrderItem> getItems() { return Collections.unmodifiableList(items); }
}
