package com.NTFOODS.Api_tanty.modules.stock.domain.product.aggregate;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.Money;
import com.NTFOODS.Api_tanty.modules.stock.domain.product.entity.ProductVariant;
import com.NTFOODS.Api_tanty.modules.stock.domain.product.enums.Category;
import com.NTFOODS.Api_tanty.modules.stock.domain.product.enums.UnitType;
import com.NTFOODS.Api_tanty.modules.stock.domain.product.valueobject.ProductSku;

/**
 * Agrégat racine pour un produit (SKU). Correspond à la table `product`.
 */
public class ProductAggregate {
    private final ProductSku sku;
    private final ProductVariant variant;
    private String barcode;
    private Category category;
    private UnitType unit;
    private Money unitPrice;
    private Integer leadTimeDays;   // délai approvisionnement (jours)
    private Integer safetyStockDays;
    private boolean active;

    public ProductAggregate(ProductSku sku, ProductVariant variant, String barcode,
                            Category category, UnitType unit, Money unitPrice,
                            Integer leadTimeDays, Integer safetyStockDays) {
        this.sku = sku;
        this.variant = variant;
        this.barcode = barcode;
        this.category = category;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.leadTimeDays = leadTimeDays;
        this.safetyStockDays = safetyStockDays;
        this.active = true;
    }

    // Getters
    public ProductSku getSku() { return sku; }
    public ProductVariant getVariant() { return variant; }
    public Category getCategory() { return category; }
    public UnitType getUnit() { return unit; }
    public Money getUnitPrice() { return unitPrice; }
    public Integer getLeadTimeDays() { return leadTimeDays; }
    public Integer getSafetyStockDays() { return safetyStockDays; }
    public boolean isActive() { return active; }

    // Méthodes métier
    public void updatePrice(Money newPrice) { this.unitPrice = newPrice; }
    public void deactivate() { this.active = false; }
    public void updateLeadTime(Integer days) { this.leadTimeDays = days; }
}