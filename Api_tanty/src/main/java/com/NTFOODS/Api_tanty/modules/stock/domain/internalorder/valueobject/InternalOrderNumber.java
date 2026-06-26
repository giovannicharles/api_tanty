package com.NTFOODS.Api_tanty.modules.stock.domain.internalorder.valueobject;

public class InternalOrderNumber {
    private final String value;
    public InternalOrderNumber(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Numéro de commande interne invalide");
        this.value = value;
    }
    public String getValue() { return value; }
}