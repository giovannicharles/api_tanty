package com.NTFOODS.Api_tanty.modules.stock.domain.dotation.aggregate;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.CommercialId;
import com.NTFOODS.Api_tanty.modules.stock.domain.dotation.entity.InfoProduitsLine;
import com.NTFOODS.Api_tanty.modules.stock.domain.dotation.enums.InfoStatus;
import com.NTFOODS.Api_tanty.modules.stock.domain.dotation.valueobject.InfoProduitsId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Agrégat racine pour une fiche de dotation (InfoProduits). */
public class InfoProduitsAggregate {
    private final InfoProduitsId id;
    private final CommercialId commercialId;
    private final LocalDate date;
    private InfoStatus status;
    private final UserId preparedBy;
    private UserId cashierValidatedBy;
    private UserId accountantValidatedBy;
    private final List<InfoProduitsLine> lines;

    private InfoProduitsAggregate(InfoProduitsId id, CommercialId commercialId, LocalDate date,
                                  List<InfoProduitsLine> lines, UserId preparedBy) {
        this.id = id;
        this.commercialId = commercialId;
        this.date = date;
        this.lines = new ArrayList<>(lines);
        this.preparedBy = preparedBy;
        this.status = InfoStatus.DRAFT;
    }

    public static InfoProduitsAggregate create(InfoProduitsId id, CommercialId commercialId, LocalDate date,
                                               List<InfoProduitsLine> lines, UserId preparedBy) {
        if (lines == null || lines.isEmpty())
            throw new IllegalArgumentException("Au moins une ligne requise");
        return new InfoProduitsAggregate(id, commercialId, date, lines, preparedBy);
    }

    // Sortie physique : diminue stock central, augmente stock mobile
    public void releaseToCommercial() {
        if (status != InfoStatus.DRAFT)
            throw new IllegalStateException("Seul un brouillon peut être sorti");
        this.status = InfoStatus.PENDING_CASH;
    }

    public void validateByCashier(UserId cashier) {
        if (status != InfoStatus.PENDING_CASH)
            throw new IllegalStateException("En attente de caisse requis");
        this.cashierValidatedBy = cashier;
        this.status = InfoStatus.PENDING_ACCOUNTANT;
    }

    public void validateByAccountant(UserId accountant) {
        if (status != InfoStatus.PENDING_ACCOUNTANT)
            throw new IllegalStateException("En attente comptable requis");
        this.accountantValidatedBy = accountant;
        this.status = InfoStatus.CLOSED;
    }

    // Getters
}