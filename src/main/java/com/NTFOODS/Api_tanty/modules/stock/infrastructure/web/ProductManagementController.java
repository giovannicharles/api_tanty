package com.NTFOODS.Api_tanty.modules.stock.infrastructure.web;

import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * ProductManagementController - CRUD complet pour la gestion du catalogue produit.
 * Centralise la configuration des Marques, Gammes, Variantes, Produits et Prix.
 * Une fois configurés ici, les autres modules (stock, ventes, valorisation) s'adaptent automatiquement.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductManagementController {

    private final BrandJpaRepository brandRepo;
    private final ProductLineJpaRepository lineRepo;
    private final ProductVariantJpaRepository variantRepo;
    private final ProductJpaRepository productRepo;
    private final ProductPriceJpaRepository priceRepo;

    // ════════════════════════════════════════════════════════════
    //  BRANDS (Marques)
    // ════════════════════════════════════════════════════════════

    @GetMapping("/brands")
    public ResponseEntity<List<BrandJpaEntity>> getAllBrands() {
        return ResponseEntity.ok(brandRepo.findAll());
    }

    @GetMapping("/brands/active")
    public ResponseEntity<List<BrandJpaEntity>> getActiveBrands() {
        return ResponseEntity.ok(brandRepo.findByActiveTrue());
    }

    @PostMapping("/brands")
    public ResponseEntity<BrandJpaEntity> createBrand(@RequestBody BrandRequest req) {
        if (req.name == null || req.name.isBlank()) throw new IllegalArgumentException("Le nom est requis");
        if (req.code == null || req.code.isBlank()) throw new IllegalArgumentException("Le code est requis");
        BrandJpaEntity b = new BrandJpaEntity();
        b.setName(req.name);
        b.setCode(req.code);
        b.setActive(req.active != null ? req.active : true);
        return ResponseEntity.ok(brandRepo.save(b));
    }

    @PutMapping("/brands/{id}")
    public ResponseEntity<BrandJpaEntity> updateBrand(@PathVariable Long id, @RequestBody BrandRequest req) {
        BrandJpaEntity b = brandRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Marque introuvable: " + id));
        if (req.name != null) b.setName(req.name);
        if (req.code != null) b.setCode(req.code);
        if (req.active != null) b.setActive(req.active);
        return ResponseEntity.ok(brandRepo.save(b));
    }

    @DeleteMapping("/brands/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        List<ProductLineJpaEntity> lines = lineRepo.findByBrandId(id);
        if (!lines.isEmpty()) throw new IllegalStateException("Impossible de supprimer: " + lines.size() + " gamme(s) associée(s)");
        brandRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ════════════════════════════════════════════════════════════
    //  PRODUCT LINES (Gammes)
    // ════════════════════════════════════════════════════════════

    @GetMapping("/product-lines")
    public ResponseEntity<List<ProductLineJpaEntity>> getAllLines(@RequestParam(required = false) Long brandId) {
        if (brandId != null) return ResponseEntity.ok(lineRepo.findByBrandId(brandId));
        return ResponseEntity.ok(lineRepo.findAll());
    }

    @PostMapping("/product-lines")
    public ResponseEntity<ProductLineJpaEntity> createLine(@RequestBody ProductLineRequest req) {
        if (req.name == null || req.name.isBlank()) throw new IllegalArgumentException("Le nom est requis");
        if (req.brandId == null) throw new IllegalArgumentException("La marque est requise");
        brandRepo.findById(req.brandId).orElseThrow(() -> new IllegalArgumentException("Marque introuvable: " + req.brandId));
        ProductLineJpaEntity l = new ProductLineJpaEntity();
        l.setBrandId(req.brandId);
        l.setName(req.name);
        l.setCode(req.code != null ? req.code : "");
        l.setActive(req.active != null ? req.active : true);
        return ResponseEntity.ok(lineRepo.save(l));
    }

    @PutMapping("/product-lines/{id}")
    public ResponseEntity<ProductLineJpaEntity> updateLine(@PathVariable Long id, @RequestBody ProductLineRequest req) {
        ProductLineJpaEntity l = lineRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Gamme introuvable: " + id));
        if (req.brandId != null) l.setBrandId(req.brandId);
        if (req.name != null) l.setName(req.name);
        if (req.code != null) l.setCode(req.code);
        if (req.active != null) l.setActive(req.active);
        return ResponseEntity.ok(lineRepo.save(l));
    }

    @DeleteMapping("/product-lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        List<ProductVariantJpaEntity> variants = variantRepo.findByProductLineId(id);
        if (!variants.isEmpty()) throw new IllegalStateException("Impossible de supprimer: " + variants.size() + " variante(s) associée(s)");
        lineRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ════════════════════════════════════════════════════════════
    //  PRODUCT VARIANTS (Variétés)
    // ════════════════════════════════════════════════════════════

    @GetMapping("/product-variants")
    public ResponseEntity<List<ProductVariantJpaEntity>> getAllVariants(@RequestParam(required = false) Long productLineId) {
        if (productLineId != null) return ResponseEntity.ok(variantRepo.findByProductLineId(productLineId));
        return ResponseEntity.ok(variantRepo.findAll());
    }

    @PostMapping("/product-variants")
    public ResponseEntity<ProductVariantJpaEntity> createVariant(@RequestBody ProductVariantRequest req) {
        if (req.name == null || req.name.isBlank()) throw new IllegalArgumentException("Le nom est requis");
        if (req.productLineId == null) throw new IllegalArgumentException("La gamme est requise");
        lineRepo.findById(req.productLineId).orElseThrow(() -> new IllegalArgumentException("Gamme introuvable: " + req.productLineId));
        ProductVariantJpaEntity v = new ProductVariantJpaEntity();
        v.setProductLineId(req.productLineId);
        v.setName(req.name);
        v.setCode(req.code != null ? req.code : "");
        return ResponseEntity.ok(variantRepo.save(v));
    }

    @PutMapping("/product-variants/{id}")
    public ResponseEntity<ProductVariantJpaEntity> updateVariant(@PathVariable Long id, @RequestBody ProductVariantRequest req) {
        ProductVariantJpaEntity v = variantRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Variante introuvable: " + id));
        if (req.productLineId != null) v.setProductLineId(req.productLineId);
        if (req.name != null) v.setName(req.name);
        if (req.code != null) v.setCode(req.code);
        return ResponseEntity.ok(variantRepo.save(v));
    }

    @DeleteMapping("/product-variants/{id}")
    public ResponseEntity<Void> deleteVariant(@PathVariable Long id) {
        List<ProductJpaEntity> products = productRepo.findByVariantId(id);
        if (!products.isEmpty()) throw new IllegalStateException("Impossible de supprimer: " + products.size() + " produit(s) associé(s)");
        variantRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ════════════════════════════════════════════════════════════
    //  PRODUCTS (Produits)
    // ════════════════════════════════════════════════════════════

    @GetMapping
    public ResponseEntity<List<ProductJpaEntity>> getAllProducts(
            @RequestParam(required = false) String materialType,
            @RequestParam(required = false) Long variantId,
            @RequestParam(required = false) Boolean active) {
        if (materialType != null) return ResponseEntity.ok(productRepo.findByMaterialType(materialType));
        if (variantId != null) return ResponseEntity.ok(productRepo.findByVariantId(variantId));
        if (active != null && active) return ResponseEntity.ok(productRepo.findByActiveTrue());
        return ResponseEntity.ok(productRepo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductJpaEntity> getProductById(@PathVariable Long id) {
        return productRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable: " + id));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductJpaEntity> getProductBySku(@PathVariable String sku) {
        return productRepo.findBySku(sku)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable (SKU): " + sku));
    }

    @PostMapping
    public ResponseEntity<ProductJpaEntity> createProduct(@RequestBody ProductRequest req) {
        if (req.sku == null || req.sku.isBlank()) throw new IllegalArgumentException("Le SKU est requis");
        if (productRepo.findBySku(req.sku).isPresent()) throw new IllegalStateException("SKU déjà existant: " + req.sku);
        ProductJpaEntity p = new ProductJpaEntity();
        applyProductFields(p, req);
        p.setActive(req.active != null ? req.active : true);
        return ResponseEntity.ok(productRepo.save(p));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductJpaEntity> updateProduct(@PathVariable Long id, @RequestBody ProductRequest req) {
        ProductJpaEntity p = productRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Produit introuvable: " + id));
        applyProductFields(p, req);
        if (req.active != null) p.setActive(req.active);
        return ResponseEntity.ok(productRepo.save(p));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        List<ProductPriceJpaEntity> prices = priceRepo.findByProductId(id);
        if (!prices.isEmpty()) priceRepo.deleteAll(prices);
        productRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void applyProductFields(ProductJpaEntity p, ProductRequest req) {
        if (req.sku != null) p.setSku(req.sku);
        if (req.variantId != null) p.setVariantId(req.variantId);
        if (req.barcode != null) p.setBarcode(req.barcode);
        if (req.category != null) p.setCategory(req.category);
        if (req.unit != null) p.setUnit(req.unit);
        if (req.unitPriceAmount != null) p.setUnitPriceAmount(req.unitPriceAmount);
        if (req.leadTimeDays != null) p.setLeadTimeDays(req.leadTimeDays);
        if (req.safetyStockDays != null) p.setSafetyStockDays(req.safetyStockDays);
        if (req.packagingType != null) p.setPackagingType(req.packagingType);
        if (req.quantityPerCarton != null) p.setQuantityPerCarton(req.quantityPerCarton);
        if (req.unitWeight != null) p.setUnitWeight(req.unitWeight);
        if (req.volume != null) p.setVolume(req.volume);
        if (req.cartonsPerAssortiment != null) p.setCartonsPerAssortiment(req.cartonsPerAssortiment);
        if (req.materialType != null) p.setMaterialType(req.materialType);
    }

    // ════════════════════════════════════════════════════════════
    //  PRODUCT PRICES (Prix par type)
    // ════════════════════════════════════════════════════════════

    @GetMapping("/{productId}/prices")
    public ResponseEntity<List<ProductPriceJpaEntity>> getPrices(@PathVariable Long productId) {
        return ResponseEntity.ok(priceRepo.findByProductId(productId));
    }

    @GetMapping("/{productId}/prices/active")
    public ResponseEntity<List<ProductPriceJpaEntity>> getActivePrices(@PathVariable Long productId) {
        return ResponseEntity.ok(priceRepo.findByProductIdAndActiveTrue(productId));
    }

    @GetMapping("/{productId}/prices/{priceType}")
    public ResponseEntity<ProductPriceJpaEntity> getPriceByType(@PathVariable Long productId, @PathVariable String priceType) {
        return priceRepo.findByProductIdAndPriceTypeAndActiveTrue(productId, priceType)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("Prix non trouvé: " + priceType + " pour produit " + productId));
    }

    @PostMapping("/{productId}/prices")
    public ResponseEntity<ProductPriceJpaEntity> createPrice(@PathVariable Long productId, @RequestBody PriceRequest req) {
        productRepo.findById(productId).orElseThrow(() -> new IllegalArgumentException("Produit introuvable: " + productId));
        if (req.priceType == null || req.priceType.isBlank()) throw new IllegalArgumentException("Le type de prix est requis");
        if (req.price == null) throw new IllegalArgumentException("Le prix est requis");
        ProductPriceJpaEntity p = new ProductPriceJpaEntity();
        p.setProductId(productId);
        p.setPriceType(req.priceType);
        p.setPrice(req.price);
        p.setCurrency(req.currency != null ? req.currency : "XAF");
        p.setValidFrom(req.validFrom != null ? req.validFrom : LocalDateTime.now());
        p.setValidTo(req.validTo);
        p.setMinQuantity(req.minQuantity != null ? req.minQuantity : BigDecimal.ZERO);
        p.setActive(req.active != null ? req.active : true);
        p.setCreatedAt(LocalDateTime.now());
        p.setUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok(priceRepo.save(p));
    }

    @PutMapping("/{productId}/prices/{priceId}")
    public ResponseEntity<ProductPriceJpaEntity> updatePrice(@PathVariable Long productId, @PathVariable Long priceId, @RequestBody PriceRequest req) {
        ProductPriceJpaEntity p = priceRepo.findById(priceId)
                .orElseThrow(() -> new IllegalArgumentException("Prix introuvable: " + priceId));
        if (!p.getProductId().equals(productId)) throw new IllegalArgumentException("Le prix ne appartient pas à ce produit");
        if (req.priceType != null) p.setPriceType(req.priceType);
        if (req.price != null) p.setPrice(req.price);
        if (req.currency != null) p.setCurrency(req.currency);
        if (req.validFrom != null) p.setValidFrom(req.validFrom);
        if (req.validTo != null) p.setValidTo(req.validTo);
        if (req.minQuantity != null) p.setMinQuantity(req.minQuantity);
        if (req.active != null) p.setActive(req.active);
        p.setUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok(priceRepo.save(p));
    }

    @DeleteMapping("/{productId}/prices/{priceId}")
    public ResponseEntity<Void> deletePrice(@PathVariable Long productId, @PathVariable Long priceId) {
        ProductPriceJpaEntity p = priceRepo.findById(priceId)
                .orElseThrow(() -> new IllegalArgumentException("Prix introuvable: " + priceId));
        if (!p.getProductId().equals(productId)) throw new IllegalArgumentException("Le prix ne appartient pas à ce produit");
        priceRepo.deleteById(priceId);
        return ResponseEntity.noContent().build();
    }

    // ════════════════════════════════════════════════════════════
    //  STATS (pour le tableau de bord du module)
    // ════════════════════════════════════════════════════════════

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        long brands = brandRepo.count();
        long lines = lineRepo.count();
        long variants = variantRepo.count();
        long products = productRepo.count();
        long activeProducts = productRepo.findByActiveTrue().size();
        long prices = priceRepo.count();
        long produitsFinis = productRepo.findByMaterialType("PRODUIT_FINI").size();
        long matieresPremieres = productRepo.findByMaterialType("MATIERE_PREMIERE").size();
        long consommables = productRepo.findByMaterialType("CONSOMMABLE").size();
        return ResponseEntity.ok(Map.of(
                "brands", brands,
                "productLines", lines,
                "variants", variants,
                "products", products,
                "activeProducts", activeProducts,
                "prices", prices,
                "produitsFinis", produitsFinis,
                "matieresPremieres", matieresPremieres,
                "consommables", consommables
        ));
    }

    // ════════════════════════════════════════════════════════════
    //  REQUEST DTOs
    // ════════════════════════════════════════════════════════════

    public static class BrandRequest {
        public String name;
        public String code;
        public Boolean active;
    }

    public static class ProductLineRequest {
        public Long brandId;
        public String name;
        public String code;
        public Boolean active;
    }

    public static class ProductVariantRequest {
        public Long productLineId;
        public String name;
        public String code;
    }

    public static class ProductRequest {
        public String sku;
        public Long variantId;
        public String barcode;
        public String category;
        public String unit;
        public BigDecimal unitPriceAmount;
        public Integer leadTimeDays;
        public Integer safetyStockDays;
        public Boolean active;
        public String packagingType;
        public Integer quantityPerCarton;
        public BigDecimal unitWeight;
        public String volume;
        public Integer cartonsPerAssortiment;
        public String materialType;
    }

    public static class PriceRequest {
        public String priceType;
        public BigDecimal price;
        public String currency;
        public LocalDateTime validFrom;
        public LocalDateTime validTo;
        public BigDecimal minQuantity;
        public Boolean active;
    }
}
