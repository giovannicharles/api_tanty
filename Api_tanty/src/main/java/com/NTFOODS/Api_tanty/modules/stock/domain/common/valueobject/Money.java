package com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject;

import java.math.BigDecimal;
import java.util.Currency;

public final class Money {
    private static final Currency XAF = Currency.getInstance("XAF");
    private final BigDecimal amount;

    public Money(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Montant négatif interdit");
        this.amount = amount;
    }

    public BigDecimal getAmount() { return amount; }
    public Currency getCurrency() { return XAF; }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }
    // equals, hashCode
}
