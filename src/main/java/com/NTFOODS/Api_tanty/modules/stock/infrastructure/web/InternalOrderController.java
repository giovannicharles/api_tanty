package com.NTFOODS.Api_tanty.modules.stock.infrastructure.web;

import com.NTFOODS.Api_tanty.modules.stock.application.dto.*;
import com.NTFOODS.Api_tanty.modules.stock.application.service.InternalOrderService;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * InternalOrderController - REST API pour les commandes internes Stock → Production
 * Workflow: Gestionnaire stock crée → Chef production approuve → Livraison partielle/complète
 */
@RestController
@RequestMapping("/api/stock/internal-orders")
public class InternalOrderController {

    private static final Logger log = LoggerFactory.getLogger(InternalOrderController.class);

    private final InternalOrderService internalOrderService;

    public InternalOrderController(InternalOrderService internalOrderService) {
        this.internalOrderService = internalOrderService;
    }

    @GetMapping
    public ResponseEntity<List<InternalOrderResponse>> getAllOrders() {
        return ResponseEntity.ok(internalOrderService.getAllOrders());
    }

    @GetMapping("/active")
    public ResponseEntity<List<InternalOrderResponse>> getActiveOrders() {
        return ResponseEntity.ok(internalOrderService.getActiveOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternalOrderResponse> getOrderById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(internalOrderService.getOrderById(id));
        } catch (Exception e) {
            log.error("Erreur récupération commande {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<InternalOrderResponse>> getOrdersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(internalOrderService.getOrdersByStatus(status));
    }

    @PostMapping
    public ResponseEntity<InternalOrderResponse> createOrder(@RequestBody CreateInternalOrderRequest request) {
        try {
            InternalOrderResponse created = internalOrderService.createOrder(request);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            log.error("Erreur création commande interne: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<InternalOrderResponse> approveOrder(
            @PathVariable Long id,
            @RequestBody ApproveOrderRequest request) {
        try {
            InternalOrderResponse approved = internalOrderService.approveOrder(
                    id, new UserId(request.getApproverId()), request.getApproverName());
            return ResponseEntity.ok(approved);
        } catch (Exception e) {
            log.error("Erreur approbation commande {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<InternalOrderResponse> cancelOrder(
            @PathVariable Long id,
            @RequestBody CancelOrderRequest request) {
        try {
            InternalOrderResponse cancelled = internalOrderService.cancelOrder(
                    id, new UserId(request.getCancelledBy()), request.getReason());
            return ResponseEntity.ok(cancelled);
        } catch (Exception e) {
            log.error("Erreur annulation commande {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<InternalOrderResponse> registerDelivery(
            @PathVariable Long id,
            @RequestBody DeliverOrderRequest request) {
        try {
            InternalOrderResponse updated = internalOrderService.registerDelivery(
                    id, request.getProductId(), request.getDeliveredQty());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Erreur livraison commande {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
