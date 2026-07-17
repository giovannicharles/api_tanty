package com.NTFOODS.Api_tanty.modules.stock.infrastructure.web;

import com.NTFOODS.Api_tanty.modules.stock.application.dto.ProductCatalogResponse;
import com.NTFOODS.Api_tanty.modules.stock.application.dto.StockItemResponse;
import com.NTFOODS.Api_tanty.modules.stock.application.service.ProductCatalogService;
import com.NTFOODS.Api_tanty.modules.stock.application.service.StockItemService;
import com.NTFOODS.Api_tanty.modules.stock.application.service.StockMovementService;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockItem;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockMovement;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockMovementType;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.jpa.StockItemJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.repository.StockItemRepository;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.repository.StockLocationRepository;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * StockItemController - Consultation et mise à jour des quantités par emplacement.
 * Réécrit pour enrichir les réponses avec les informations produit complètes (code
 * SKU, désignation, unité, catégorie, type) : auparavant l'entité StockItem brute
 * était renvoyée telle quelle, ne portant que productId/productSku sans aucun moyen
 * d'afficher le nom ou la nature du produit sans un second appel manuel au catalogue.
 */
@RestController
@RequestMapping("/api/stock/items")
public class StockItemController {

    private final StockItemService stockItemService;
    private final ProductCatalogService productCatalogService;
    private final StockItemRepository stockItemRepository;
    private final StockLocationRepository stockLocationRepository;
    private final StockMovementService stockMovementService;

    public StockItemController(StockItemService stockItemService, ProductCatalogService productCatalogService,
                               StockItemRepository stockItemRepository, StockLocationRepository stockLocationRepository,
                               StockMovementService stockMovementService) {
        this.stockItemService = stockItemService;
        this.productCatalogService = productCatalogService;
        this.stockItemRepository = stockItemRepository;
        this.stockLocationRepository = stockLocationRepository;
        this.stockMovementService = stockMovementService;
    }

    @PostMapping
    public ResponseEntity<StockItemResponse> createOrUpdateStockItem(@RequestBody CreateStockItemRequest request) {
        StockLocationId stockLocationId = new StockLocationId(UUID.fromString(request.locationId));
        UserId userId = request.updatedBy != null ? new UserId(request.updatedBy) : null;

        StockItem item = stockItemService.createOrUpdateStockItem(
                stockLocationId, request.productId, request.productSku, request.packagingType,
                request.quantity, request.quantityPerCarton, request.unitWeight,
                request.volume, request.cartonsPerAssortiment, userId);

        return ResponseEntity.ok(enrich(item));
    }

    public static class CreateStockItemRequest {
        public String locationId;
        public Long productId;
        public String productSku;
        public String packagingType;
        public BigDecimal quantity;
        public BigDecimal quantityPerCarton;
        public BigDecimal unitWeight;
        public String volume;
        public Integer cartonsPerAssortiment;
        public String updatedBy;
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<StockItemResponse>> getStockItemsByLocation(@PathVariable UUID locationId) {
        StockLocationId stockLocationId = new StockLocationId(locationId);
        List<StockItem> items = stockItemService.getStockItemsByLocation(stockLocationId);
        return ResponseEntity.ok(enrichAll(items));
    }

    @GetMapping("/location/{locationId}/sku/{productSku}")
    public ResponseEntity<StockItemResponse> getStockItem(@PathVariable UUID locationId, @PathVariable String productSku) {
        StockLocationId stockLocationId = new StockLocationId(locationId);
        return stockItemService.getStockItem(stockLocationId, productSku)
                .map(this::enrich)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/location/{locationId}/sku/{productSku}/packagings")
    public ResponseEntity<List<StockItemResponse>> getAllPackagingsForProduct(
            @PathVariable UUID locationId, @PathVariable String productSku) {
        StockLocationId stockLocationId = new StockLocationId(locationId);
        List<StockItem> items = stockItemService.getAllPackagingForProduct(stockLocationId, productSku);
        return ResponseEntity.ok(enrichAll(items));
    }

    @PostMapping("/location/{locationId}/sku/{productSku}/add")
    public ResponseEntity<Void> addQuantity(
            @PathVariable UUID locationId,
            @PathVariable String productSku,
            @RequestBody QuantityUpdateRequest request) {

        StockLocationId stockLocationId = new StockLocationId(locationId);
        UserId userId = request.updatedBy != null ? new UserId(request.updatedBy) : null;

        stockItemService.addQuantity(stockLocationId, productSku, request.quantityToAdd, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/location/{locationId}/sku/{productSku}/subtract")
    public ResponseEntity<Void> subtractQuantity(
            @PathVariable UUID locationId,
            @PathVariable String productSku,
            @RequestBody QuantityUpdateRequest request) {

        StockLocationId stockLocationId = new StockLocationId(locationId);
        UserId userId = request.updatedBy != null ? new UserId(request.updatedBy) : null;

        stockItemService.subtractQuantity(stockLocationId, productSku, request.quantityToSubtract, userId);
        return ResponseEntity.ok().build();
    }

    public static class QuantityUpdateRequest {
        public BigDecimal quantityToAdd;
        public BigDecimal quantityToSubtract;
        public String updatedBy;
    }

    @GetMapping("/location/{locationId}/low-stock")
    public ResponseEntity<List<StockItemResponse>> getLowStockItems(
            @PathVariable UUID locationId,
            @RequestParam BigDecimal threshold) {

        StockLocationId stockLocationId = new StockLocationId(locationId);
        List<StockItem> items = stockItemService.getLowStockItems(stockLocationId, threshold);
        return ResponseEntity.ok(enrichAll(items));
    }

    @GetMapping("/location/{locationId}/total")
    public ResponseEntity<BigDecimal> getTotalQuantity(@PathVariable UUID locationId) {
        StockLocationId stockLocationId = new StockLocationId(locationId);
        return stockItemService.getTotalQuantity(stockLocationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok(BigDecimal.ZERO));
    }

    @PostMapping("/adjust")
    public ResponseEntity<StockItemResponse> adjustStock(@RequestBody AdjustStockRequest request) {
        StockItemJpaEntity entity = stockItemRepository.findById(request.stockLevelId)
                .orElseThrow(() -> new IllegalArgumentException("Stock item not found: " + request.stockLevelId));

        BigDecimal oldQty = entity.getQuantity();
        BigDecimal difference = request.newQuantity.subtract(oldQty);

        entity.setQuantity(request.newQuantity);
        entity.setLastUpdated(LocalDateTime.now());
        stockItemRepository.save(entity);

        // Créer un StockMovement ADJUSTMENT pour la traçabilité
        UserId userId = request.requestedBy != null ? new UserId(request.requestedBy) : null;
        String reason = request.reason != null ? request.reason : "Ajustement d'inventaire";
        String notes = String.format("Ajustement: %s → %s (diff: %s). Motif: %s",
                oldQty.stripTrailingZeros().toPlainString(),
                request.newQuantity.stripTrailingZeros().toPlainString(),
                difference.stripTrailingZeros().toPlainString(),
                reason);

        StockMovement movement = stockMovementService.createMovement(
                StockMovementType.ADJUSTMENT,
                new StockLocationId(entity.getLocationId()),
                new StockLocationId(entity.getLocationId()),
                entity.getProductId(),
                entity.getProductSku(),
                entity.getPackagingType(),
                difference.abs(),
                entity.getQuantityPerCarton(),
                userId,
                "ADJ-" + System.currentTimeMillis(),
                notes
        );
        // Valider immédiatement le mouvement
        stockMovementService.validateMovement(movement.getId(), userId);

        StockItem item = stockItemService.getStockItem(new StockLocationId(entity.getLocationId()), entity.getProductSku())
                .orElseThrow(() -> new IllegalStateException("Stock item disappeared after update"));
        return ResponseEntity.ok(enrich(item));
    }

    public static class AdjustStockRequest {
        public Long stockLevelId;
        public BigDecimal newQuantity;
        public String reason;
        public String requestedBy;
    }

    // ── TOUS LES ITEMS (tous entrepôts) ─────────────────────────

    @GetMapping("/all")
    public ResponseEntity<List<StockItemResponse>> getAllStockItems() {
        List<StockItem> items = stockItemService.getAllStockItems();
        return ResponseEntity.ok(enrichAll(items));
    }

    @PostMapping("/levels/{id}/transfer-to-buffer")
    public ResponseEntity<Void> transferToBuffer(@PathVariable Long id, @RequestParam BigDecimal quantity) {
        StockItemJpaEntity item = stockItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stock item not found: " + id));

        if (item.getQuantity().compareTo(quantity) < 0) {
            throw new IllegalStateException("Insufficient stock: have " + item.getQuantity() + ", need " + quantity);
        }

        var bufferLocs = stockLocationRepository.findByType(StockLocationType.STOCK_BUFFER);
        if (bufferLocs.isEmpty()) {
            throw new IllegalStateException("No STOCK_BUFFER location found");
        }
        UUID bufferLocationId = bufferLocs.get(0).getLocationId();

        stockItemService.subtractQuantity(new StockLocationId(item.getLocationId()), item.getProductSku(), quantity, null);
        stockItemService.addQuantity(new StockLocationId(bufferLocationId), item.getProductId(),
                item.getProductSku(), item.getPackagingType(), item.getQuantityPerCarton(), quantity, null);

        return ResponseEntity.ok().build();
    }

    // ── REAPPROVISIONNEMENT TAMPON (central → tampon) ────────────────────────

    @PostMapping("/replenish-buffer")
    public ResponseEntity<StockMovement> replenishBuffer(@RequestBody ReplenishBufferRequest request) {
        // Trouver le stock central
        var centralLocs = stockLocationRepository.findByType(StockLocationType.STOCK_CENTRAL);
        if (centralLocs.isEmpty()) {
            throw new IllegalStateException("Aucune localisation STOCK_CENTRAL trouvée");
        }
        UUID centralLocationId = centralLocs.get(0).getLocationId();

        // Trouver le tampon
        var bufferLocs = stockLocationRepository.findByType(StockLocationType.STOCK_BUFFER);
        if (bufferLocs.isEmpty()) {
            throw new IllegalStateException("Aucune localisation STOCK_BUFFER trouvée");
        }
        UUID bufferLocationId = bufferLocs.get(0).getLocationId();

        // Vérifier la disponibilité dans le stock central
        StockItemJpaEntity centralItem = stockItemRepository
                .findByLocationIdAndProductSku(centralLocationId, request.productSku)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Produit non trouvé dans le stock central: " + request.productSku));

        if (centralItem.getQuantity().compareTo(request.quantity) < 0) {
            throw new IllegalStateException(String.format(
                    "Stock central insuffisant pour %s (demandé: %s, disponible: %s)",
                    request.productSku, request.quantity.stripTrailingZeros().toPlainString(),
                    centralItem.getQuantity().stripTrailingZeros().toPlainString()));
        }

        // Créer le mouvement de stock (TRANSFER_CENTRAL_TO_BUFFER)
        UserId userId = request.requestedBy != null ? new UserId(request.requestedBy) : null;
        StockMovement movement = stockMovementService.createMovement(
                StockMovementType.TRANSFER_CENTRAL_TO_BUFFER,
                new StockLocationId(centralLocationId),
                new StockLocationId(bufferLocationId),
                centralItem.getProductId(),
                request.productSku,
                centralItem.getPackagingType(),
                request.quantity,
                centralItem.getQuantityPerCarton(),
                userId,
                "REAPPRO-TAMPON-" + System.currentTimeMillis(),
                request.notes != null ? request.notes : "Réapprovisionnement tampon depuis stock central"
        );

        // Valider immédiatement le mouvement (soustrait central, ajoute tampon)
        stockMovementService.validateMovement(movement.getId(), userId);

        return ResponseEntity.ok(movement);
    }

    public static class ReplenishBufferRequest {
        public String productSku;
        public BigDecimal quantity;
        public String requestedBy;
        public String notes;
    }

    // ── AJOUTER UN PRODUIT AU TAMPON (depuis stock central) ──────────────────

    @PostMapping("/add-to-buffer")
    public ResponseEntity<StockMovement> addToBuffer(@RequestBody AddToBufferRequest request) {
        // Trouver le stock central
        var centralLocs = stockLocationRepository.findByType(StockLocationType.STOCK_CENTRAL);
        if (centralLocs.isEmpty()) {
            throw new IllegalStateException("Aucune localisation STOCK_CENTRAL trouvée");
        }
        UUID centralLocationId = centralLocs.get(0).getLocationId();

        // Trouver le tampon
        var bufferLocs = stockLocationRepository.findByType(StockLocationType.STOCK_BUFFER);
        if (bufferLocs.isEmpty()) {
            throw new IllegalStateException("Aucune localisation STOCK_BUFFER trouvée");
        }
        UUID bufferLocationId = bufferLocs.get(0).getLocationId();

        // Vérifier la disponibilité dans le stock central
        StockItemJpaEntity centralItem = stockItemRepository
                .findByLocationIdAndProductSku(centralLocationId, request.productSku)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Produit non trouvé dans le stock central: " + request.productSku));

        BigDecimal transferQty = request.quantity != null ? request.quantity : BigDecimal.ZERO;
        if (transferQty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive");
        }
        if (centralItem.getQuantity().compareTo(transferQty) < 0) {
            throw new IllegalStateException(String.format(
                    "Stock central insuffisant pour %s (demandé: %s, disponible: %s)",
                    request.productSku, transferQty.stripTrailingZeros().toPlainString(),
                    centralItem.getQuantity().stripTrailingZeros().toPlainString()));
        }

        // Créer et valider le mouvement de transfert (upsert dans le tampon)
        UserId userId = request.requestedBy != null ? new UserId(request.requestedBy) : null;
        StockMovement movement = stockMovementService.createMovement(
                StockMovementType.TRANSFER_CENTRAL_TO_BUFFER,
                new StockLocationId(centralLocationId),
                new StockLocationId(bufferLocationId),
                centralItem.getProductId(),
                request.productSku,
                centralItem.getPackagingType(),
                transferQty,
                centralItem.getQuantityPerCarton(),
                userId,
                "ADD-BUFFER-" + System.currentTimeMillis(),
                request.notes != null ? request.notes : "Ajout produit au tampon depuis stock central"
        );
        stockMovementService.validateMovement(movement.getId(), userId);

        return ResponseEntity.ok(movement);
    }

    public static class AddToBufferRequest {
        public String productSku;
        public BigDecimal quantity;
        public String requestedBy;
        public String notes;
    }

    // ── SEUILS (DG définit, gestionnaire consulte) ────────────────────────────

    @PutMapping("/location/{locationId}/sku/{productSku}/thresholds")
    public ResponseEntity<StockItemResponse> setThresholds(
            @PathVariable UUID locationId,
            @PathVariable String productSku,
            @RequestBody SetThresholdsRequest request) {
        StockLocationId stockLocationId = new StockLocationId(locationId);
        StockItem item = stockItemService.setThresholds(stockLocationId, productSku,
                request.reorderPoint, request.safetyStock);
        return ResponseEntity.ok(enrich(item));
    }

    @PutMapping("/location/{locationId}/thresholds/batch")
    public ResponseEntity<Void> setDefaultThresholds(
            @PathVariable UUID locationId,
            @RequestBody SetThresholdsRequest request) {
        StockLocationId stockLocationId = new StockLocationId(locationId);
        stockItemService.setThresholdsByLocation(stockLocationId, request.reorderPoint, request.safetyStock);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/location/{locationId}/thresholds")
    public ResponseEntity<List<StockItemResponse>> getItemsWithThresholds(@PathVariable UUID locationId) {
        StockLocationId stockLocationId = new StockLocationId(locationId);
        List<StockItem> items = stockItemService.getItemsWithThresholds(stockLocationId);
        return ResponseEntity.ok(enrichAll(items));
    }

    public static class SetThresholdsRequest {
        public BigDecimal reorderPoint;
        public BigDecimal safetyStock;
    }

    private List<StockItemResponse> enrichAll(List<StockItem> items) {
        Map<Long, ProductCatalogResponse> products = productCatalogService.getByIds(
                items.stream().map(StockItem::getProductId).filter(java.util.Objects::nonNull).toList());
        return items.stream().map(i -> enrich(i, products.get(i.getProductId()))).toList();
    }

    private StockItemResponse enrich(StockItem item) {
        ProductCatalogResponse product = item.getProductId() != null
                ? productCatalogService.getById(item.getProductId()).orElse(null)
                : null;
        return enrich(item, product);
    }

    private StockItemResponse enrich(StockItem item, ProductCatalogResponse product) {
        StockItemResponse r = new StockItemResponse();
        r.setId(item.getId());
        r.setLocationId(item.getLocationId().value().toString());
        r.setProductId(item.getProductId());
        r.setProductSku(item.getProductSku());
        r.setPackagingType(item.getPackagingType());
        r.setQuantity(item.getQuantity());
        r.setQuantityPerCarton(item.getQuantityPerCarton());
        if (item.getQuantityPerCarton() != null && item.getQuantityPerCarton().compareTo(BigDecimal.ZERO) > 0) {
            r.setCartons(item.getQuantity().divide(item.getQuantityPerCarton(), 2, RoundingMode.HALF_UP));
        }
        r.setUnitWeight(item.getUnitWeight());
        r.setVolume(item.getVolume());
        r.setCartonsPerAssortiment(item.getCartonsPerAssortiment());
        r.setReorderPoint(item.getReorderPoint());
        r.setSafetyStock(item.getSafetyStock());
        r.setLastUpdated(item.getLastUpdated());
        r.setLastUpdatedBy(item.getLastUpdatedBy() != null ? item.getLastUpdatedBy().getMatricule() : null);

        if (product != null) {
            r.setProductName(product.getDesignation());
            r.setProductUnit(product.getUnit());
            r.setProductCategory(product.getCategory());
            r.setMaterialType(product.getMaterialType());
            r.setProductLineName(product.getProductLineName());
            r.setBrandName(product.getBrandName());
        }
        return r;
    }
}
