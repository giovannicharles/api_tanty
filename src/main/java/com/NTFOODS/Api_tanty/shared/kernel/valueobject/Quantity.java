package com.NTFOODS.Api_tanty.shared.kernel.valueobject;

import java.math.BigDecimal;

public final class Quantity {
    private final BigDecimal value;
    private final String unit;

    public Quantity(BigDecimal value, String unit){
        if(value == null || value.compareTo(BigDecimal.ZERO)<0)
            throw new IllegalArgumentException("la quantité ne peut pas être négative");
        this.value=value;
        this.unit=unit;
    }
    public BigDecimal getValue(){return value;}
    public String getUnit(){return unit;}
    public Quantity add(Quantity other){
        if(!this.unit.equals(other.unit))
            throw new IllegalArgumentException("Unités incompatibles");
        return new Quantity(this.value.add(other.value),this.unit);
    }
    public Quantity subtract(Quantity other) {
        if (!this.unit.equals(other.unit))
            throw new IllegalArgumentException("Unités incompatibles");
        return new Quantity(this.value.subtract(other.value), this.unit);
    }

    //equals, hashcode à compléter.......


}
