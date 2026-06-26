package com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject;

public final class ReceiptNumber {
    private final String value;
    public ReceiptNumber(String value){
        if(value == null || value.isBlank())
            throw  new IllegalArgumentException("Le numéro de réception ne peut pas être vide.");

        this.value=value;
    }

    public String getValue(){return value;}
}
