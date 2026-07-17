package com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject;

import java.util.UUID;

/**
 * StockLocationId - Identifiant unique d'une localisation de stock
 */
public record StockLocationId(UUID value) {
    
    public StockLocationId {
        if (value == null) {
            throw new IllegalArgumentException("StockLocationId cannot be null");
        }
    }
    
    public static StockLocationId generate() {
        return new StockLocationId(UUID.randomUUID());
    }
    
    public static StockLocationId of(String value) {
        return new StockLocationId(UUID.fromString(value));
    }
}
