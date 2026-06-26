package com.NTFOODS.Api_tanty.modules.stock.domain.product.entity;

import lombok.Data;

import java.util.Objects;

/** Variété (ex: TBSA, TBSN). */
@Data
public class ProductVariant {
    private final Long id;
    private final ProductLine productLine;
    private String name;
    private String code;

    public ProductVariant(Long id, ProductLine productLine, String name, String code) {
        this.id = id;
        this.productLine = productLine;
        this.name = name;
        this.code = code;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductVariant that = (ProductVariant) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}