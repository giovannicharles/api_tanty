package com.NTFOODS.Api_tanty.modules.stock.domain.supplier.aggregate;

public class SupplierAggregate {
    private final Long id;
    private String name;
    private String contact;
    private Integer leadTimeDays; // délai de livraison (ex: 150 jours pour la Chine)
    private boolean active;

    public SupplierAggregate(Long id, String name, String contact, Integer leadTimeDays) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.leadTimeDays = leadTimeDays;
        this.active = true;
    }
}
