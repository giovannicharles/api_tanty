package com.NTFOODS.Api_tanty.shared.kernel.valueobject;

import java.util.Objects;

public final class ProductId {
    private final Long value;
    public ProductId(Long value){
        if(value==null || value <= 0){
            throw new IllegalArgumentException("L'identifiant du produit est invalide");

        }
        this.value=value;

    }
    public Long getValue(){
        return value;
    }

    @Override
    public boolean equals(Object o){
        if(this == o ) return true;
        if(o == null || getClass() != o.getClass()) return false;
        ProductId productId= (ProductId) o;
        return Objects.equals(value, productId.getValue());
    }
    @Override
    public int hashCode(){
        return Objects.hash(value);
    }
}
