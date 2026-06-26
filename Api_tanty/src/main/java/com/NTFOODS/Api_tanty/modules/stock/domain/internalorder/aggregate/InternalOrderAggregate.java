package com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.aggregate;

import com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.entity.InternalOrderItem;
import com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.enums.InternalOrderStatus;
import com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.valueobject.InternalOrderNumber;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Commande interne du stock à la production. */
public class InternalOrderAggregate {
    private final InternalOrderNumber orderNumber;
    private final LocalDate orderDate;
    private InternalOrderStatus status;
    private final UserId requestedBy;   // gestionnaire de stock
    private UserId approvedBy;          // chef production
    private final List<InternalOrderItem> items;




    private InternalOrderAggregate(InternalOrderNumber orderNumber, LocalDate orderDate,
                                   List<InternalOrderItem> items, UserId requestedBy) {
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.items = new ArrayList<>(items);
        this.requestedBy = requestedBy;
        this.status = InternalOrderStatus.DRAFT;
    }

    public static InternalOrderAggregate create(InternalOrderNumber orderNumber, LocalDate orderDate,
                                                List<InternalOrderItem> items, UserId requestedBy) {
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("Au moins un article requis");
        return new InternalOrderAggregate(orderNumber, orderDate, items, requestedBy);
    }

    public void approve(UserId approver) {
        if (status != InternalOrderStatus.DRAFT)
            throw new IllegalStateException("Seul un brouillon peut être approuvé");
        this.approvedBy = approver;
        this.status = InternalOrderStatus.APPROVED;
    }

    public void registerDelivery(ProductId productId, Quantity qty) {
        InternalOrderItem item = findItem(productId);
        if (item == null) throw new IllegalArgumentException("Produit non trouvé");
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
}
