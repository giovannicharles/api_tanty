package com.NTFOODS.Api_tanty.modules.stock.domain.product.valueobject;

public final class ProductSku {
    private final String value;
    public ProductSku(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("SKU ne peut pas être vide");
        this.value = value;
    }
    public String getValue() { return value; }
    // equals, hashCode
}
