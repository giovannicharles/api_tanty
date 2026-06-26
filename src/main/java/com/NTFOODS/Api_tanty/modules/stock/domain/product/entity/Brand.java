package com.NTFOODS.Api_tanty.modules.stock.domain.product.entity;

import java.util.Objects;

public class Brand {
    private final Long id;
    private String name;
    private String code;
    private boolean active;

    public Brand(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.active = true;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public boolean isActive() { return active; }

    public void activate() { this.active = true; }
    public void deactivate() { this.active = false; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Brand brand = (Brand) o;
        return Objects.equals(id, brand.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
