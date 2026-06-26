package com.NTFOODS.Api_tanty.modules.stock.application.product.command;

import com.NTFOODS.Api_tanty.modules.stock.domain.product.enums.Category;
import com.NTFOODS.Api_tanty.modules.stock.domain.product.enums.UnitType;

import java.math.BigDecimal;

public class CreateProductCommand {
    private final String sku;
    private final Long variantId;
    private final String barcode;
    private final Category category;
    private final UnitType unit;
    private final BigDecimal unitPrice;
    private final Integer leadTimeDays;
    private final Integer safetyStockDays;

    public CreateProductCommand(String sku, Long variantId, String barcode, Category category, UnitType unit, BigDecimal unitPrice, Integer leadTimeDays, Integer safetyStockDays) {
        this.sku = sku;
        this.variantId = variantId;
        this.barcode = barcode;
        this.category = category;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.leadTimeDays = leadTimeDays;
        this.safetyStockDays = safetyStockDays;
    }

    // constructeur, getters...
}