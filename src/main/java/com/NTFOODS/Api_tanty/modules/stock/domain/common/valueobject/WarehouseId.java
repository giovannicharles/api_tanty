package com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject;

import java.util.Objects;

public final class WarehouseId {
    private final Long value;

    public WarehouseId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Identifiant entrepôt invalide");
        }
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarehouseId that = (WarehouseId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
