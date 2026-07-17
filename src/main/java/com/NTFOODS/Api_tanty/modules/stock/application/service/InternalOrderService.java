package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.application.dto.CreateInternalOrderRequest;
import com.NTFOODS.Api_tanty.modules.stock.application.dto.InternalOrderResponse;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockLocation;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockMovementType;
import com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.aggregate.InternalOrderAggregate;
import com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.entity.InternalOrderItem;
import com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.repository.InternalOrderRepository;
import com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.valueobject.InternalOrderNumber;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * InternalOrderService - Gestion des commandes internes Stock → Production
 * Workflow: Gestionnaire stock crée → Chef production approuve → Livraison partielle/complète
 *
 * Toute la logique métier passe par l'InternalOrderAggregate (domain).
 * Le repository domaine (port) est injecté, l'implémentation JPA est un adapter.
 */
@Service
@Transactional
public class InternalOrderService {

    private static final Logger log = LoggerFactory.getLogger(InternalOrderService.class);

    private final InternalOrderRepository orderRepository;
    private final StockMovementService stockMovementService;
    private final StockLocationService stockLocationService;

    public InternalOrderService(InternalOrderRepository orderRepository,
                                StockMovementService stockMovementService,
                                StockLocationService stockLocationService) {
        this.orderRepository = orderRepository;
        this.stockMovementService = stockMovementService;
        this.stockLocationService = stockLocationService;
    }

    /**
     * Crée une commande interne (par le gestionnaire de stock)
     */
    public InternalOrderResponse createOrder(CreateInternalOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Au moins un article est requis");
        }

        List<InternalOrderItem> domainItems = new ArrayList<>();
        for (CreateInternalOrderRequest.ItemRequest item : request.getItems()) {
            String unit = item.getProductUnit() != null ? item.getProductUnit() : "unite";
            domainItems.add(new InternalOrderItem(
                    new ProductId(item.getProductId()),
                    new Quantity(item.getRequestedQty(), unit),
                    item.getProductSku(),
                    item.getProductName(),
                    unit,
                    item.getPackagingType(),
                    item.getQuantityPerCarton(),
                    item.getNotes()
            ));
        }

        UserId requestedBy = new UserId(request.getRequestedBy());
        String requestedByName = request.getRequestedByName() != null ? request.getRequestedByName() : request.getRequestedBy();

        InternalOrderNumber orderNumber = new InternalOrderNumber(generateOrderNumber());
        InternalOrderAggregate aggregate = InternalOrderAggregate.create(
                orderNumber, LocalDate.now(), domainItems, requestedBy, requestedByName, request.getNotes());

        InternalOrderAggregate saved = orderRepository.save(aggregate);
        log.info("Commande interne créée: {} par {}", saved.getOrderNumber().getValue(), requestedBy.getMatricule());
        return toResponse(saved);
    }

    /**
     * Le Chef de Production approuve la commande
     */
    public InternalOrderResponse approveOrder(Long orderId, UserId approverId, String approverName) {
        InternalOrderAggregate aggregate = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée: " + orderId));

        aggregate.approve(approverId, approverName);

        InternalOrderAggregate saved = orderRepository.save(aggregate);
        log.info("Commande interne approuvée: {} par {}", orderId, approverId.getMatricule());
        return toResponse(saved);
    }

    /**
     * Annulation d'une commande
     */
    public InternalOrderResponse cancelOrder(Long orderId, UserId cancelledBy, String reason) {
        InternalOrderAggregate aggregate = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée: " + orderId));

        aggregate.cancel(cancelledBy, reason);

        InternalOrderAggregate saved = orderRepository.save(aggregate);
        log.info("Commande interne annulée: {} par {} - Raison: {}", orderId, cancelledBy.getMatricule(), reason);
        return toResponse(saved);
    }

    /**
     * Enregistre une livraison (partielle ou complète) pour un produit donné
     */
    public InternalOrderResponse registerDelivery(Long orderId, Long productId, BigDecimal deliveredQty) {
        InternalOrderAggregate aggregate = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée: " + orderId));

        // Find the item to get its unit and product info
        InternalOrderItem item = aggregate.getItems().stream()
                .filter(i -> i.getProductId().getValue().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Produit " + productId + " non trouvé dans la commande"));

        Quantity qty = new Quantity(deliveredQty, item.getRequestedQty().getUnit());
        aggregate.registerDelivery(new ProductId(productId), qty);

        InternalOrderAggregate saved = orderRepository.save(aggregate);

        // ── Mise à jour du stock : créer + valider un mouvement RECEPTION_PRODUCTION ──
        StockLocation stockCentral = stockLocationService.getLocationsByType(StockLocationType.STOCK_CENTRAL)
                .stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Aucun emplacement STOCK_CENTRAL configuré"));

        String reference = saved.getOrderNumber().getValue() + "-LIV-" + productId;
        String notes = String.format("Livraison commande interne %s - %s", saved.getOrderNumber().getValue(), item.getProductName());

        var movement = stockMovementService.createMovement(
                StockMovementType.RECEPTION_PRODUCTION,
                null,
                stockCentral.getId(),
                productId,
                item.getProductSku(),
                item.getPackagingType(),
                deliveredQty,
                item.getQuantityPerCarton(),
                saved.getRequestedBy(),
                reference,
                notes
        );
        stockMovementService.validateMovement(movement.getId(), saved.getRequestedBy());

        log.info("Livraison enregistrée pour commande {} - Produit {} - Qté: {} - Statut: {} - Mouvement stock: {}",
                orderId, productId, deliveredQty, saved.getStatus(), movement.getId());
        return toResponse(saved);
    }

    /**
     * Récupère toutes les commandes
     */
    public List<InternalOrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une commande par ID
     */
    public InternalOrderResponse getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée: " + id));
    }

    /**
     * Récupère les commandes actives (DRAFT + APPROVED + PARTIALLY_DELIVERED)
     */
    public List<InternalOrderResponse> getActiveOrders() {
        return orderRepository.findActive().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les commandes par statut
     */
    public List<InternalOrderResponse> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Conversion Aggregate → Response DTO ───────────────────

    private InternalOrderResponse toResponse(InternalOrderAggregate agg) {
        InternalOrderResponse resp = new InternalOrderResponse();
        resp.setOrderNumber(agg.getOrderNumber().getValue());
        resp.setOrderDate(agg.getOrderDate());
        resp.setStatus(agg.getStatus().name());
        resp.setRequestedBy(agg.getRequestedBy().getMatricule());
        resp.setRequestedByName(agg.getRequestedByName());
        resp.setCreatedAt(agg.getCreatedAt());
        resp.setNotes(agg.getNotes());

        if (agg.getApprovedBy() != null) {
            resp.setApprovedBy(agg.getApprovedBy().getMatricule());
            resp.setApprovedByName(agg.getApprovedByName());
            resp.setApprovedAt(agg.getApprovedAt());
        }
        if (agg.getCancelledBy() != null) {
            resp.setCancelledBy(agg.getCancelledBy().getMatricule());
            resp.setCancelledReason(agg.getCancelledReason());
            resp.setCancelledAt(agg.getCancelledAt());
        }

        List<InternalOrderResponse.ItemResponse> itemResponses = new ArrayList<>();
        for (InternalOrderItem item : agg.getItems()) {
            InternalOrderResponse.ItemResponse ir = new InternalOrderResponse.ItemResponse();
            ir.setProductId(item.getProductId().getValue());
            ir.setProductSku(item.getProductSku());
            ir.setProductName(item.getProductName());
            ir.setProductUnit(item.getProductUnit());
            ir.setPackagingType(item.getPackagingType());
            ir.setQuantityPerCarton(item.getQuantityPerCarton());
            ir.setRequestedQty(item.getRequestedQty().getValue());
            ir.setDeliveredQty(item.getDeliveredQty().getValue());
            ir.setFullyDelivered(item.isFullyDelivered());
            ir.setNotes(item.getNotes());
            itemResponses.add(ir);
        }
        resp.setItems(itemResponses);

        return resp;
    }

    private String generateOrderNumber() {
        return "CMD-INT-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }

}
