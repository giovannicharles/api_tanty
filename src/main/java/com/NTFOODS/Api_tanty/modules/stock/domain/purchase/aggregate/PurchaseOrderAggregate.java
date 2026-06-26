package com.NTFOODS.Api_tanty.modules.stock.domain.purchase.aggregate;

import com.NTFOODS.Api_tanty.modules.stock.domain.purchase.entity.PurchaseOrderItem;
import com.NTFOODS.Api_tanty.modules.stock.domain.purchase.enums.OrderStatus;
import com.NTFOODS.Api_tanty.modules.stock.domain.supplier.aggregate.SupplierAggregate;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderAggregate {
    private final Long id;
    private final String poNumber;
    private final SupplierAggregate supplier;
    private final LocalDate orderDate;
    private OrderStatus status;
    private UserId approvedBy;
    private final List<PurchaseOrderItem> items;

    private PurchaseOrderAggregate(Long id, String poNumber, SupplierAggregate supplier,
                                   LocalDate orderDate, List<PurchaseOrderItem> items) {
        this.id = id;
        this.poNumber = poNumber;
        this.supplier = supplier;
        this.orderDate = orderDate;
        this.items = new ArrayList<>(items);
        this.status = OrderStatus.DRAFT;
    }

    public static PurchaseOrderAggregate create(Long id, String poNumber, SupplierAggregate supplier,
                                                LocalDate orderDate, List<PurchaseOrderItem> items) {
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("Au moins un article requis");
        return new PurchaseOrderAggregate(id, poNumber, supplier, orderDate, items);
    }

    public void approve(UserId approver) {
        if (status != OrderStatus.DRAFT)
            throw new IllegalStateException("Seul un brouillon peut être approuvé");
        this.approvedBy = approver;
        this.status = OrderStatus.APPROVED;
    }

    public void receive(ProductId productId, Quantity qty) {
        PurchaseOrderItem item = findItem(productId);
        if (item == null) throw new IllegalArgumentException("Produit non trouvé");
        item.addReceivedQty(qty);
        if (isFullyReceived()) this.status = OrderStatus.FULLY_RECEIVED;
        else this.status = OrderStatus.PARTIALLY_RECEIVED;
    }

    private boolean isFullyReceived() {
        return items.stream().allMatch(PurchaseOrderItem::isFullyReceived);
    }

    private PurchaseOrderItem findItem(ProductId productId) {
        return items.stream().filter(i -> i.getProductId().equals(productId)).findFirst().orElse(null);
    }
}
