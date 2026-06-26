package com.NTFOODS.Api_tanty.modules.stock.domain.product.entity;

import lombok.Data;

/** Gamme de produits. */
@Data
public class ProductLine {
    private final Long id;
    private final Brand brand;
    private String name;
    private String code;
    private boolean active;

    public ProductLine(Long id, Brand brand, String name, String code) {
        this.id = id;
        this.brand = brand;
        this.name = name;
        this.code = code;
        this.active = true;
    }
    // getters
}
