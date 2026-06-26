package com.NTFOODS.Api_tanty.modules.stock.domain.seuil.aggregate;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.WarehouseId;
import com.NTFOODS.Api_tanty.modules.stock.domain.seuil.enums.TargetType;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Politique de seuil définie par le DG. */
public class StockThresholdPolicyAggregate {
    private final Long id;
    private final TargetType targetType;
    private final Long targetId;          // id du produit, catégorie, etc. (null si ALL)
    private final WarehouseId warehouseId; // null si tous entrepôts
    private final Integer reorderDays;     // seuil en jours
    private final BigDecimal reorderPoint; // seuil absolu
    private final UserId definedBy;
    private final LocalDate effectiveDate;
    private final String justification;

    public StockThresholdPolicyAggregate(Long id, TargetType targetType, Long targetId, WarehouseId warehouseId, Integer reorderDays, BigDecimal reorderPoint, UserId definedBy, LocalDate effectiveDate, String justification) {
        this.id = id;
        this.targetType = targetType;
        this.targetId = targetId;
        this.warehouseId = warehouseId;
        this.reorderDays = reorderDays;
        this.reorderPoint = reorderPoint;
        this.definedBy = definedBy;
        this.effectiveDate = effectiveDate;
        this.justification = justification;
    }

    // constructeur, getters
}