package com.NTFOODS.Api_tanty.modules.stock.application.reception;

import com.NTFOODS.Api_tanty.modules.stock.application.dto.ReceiptItemResponse;
import com.NTFOODS.Api_tanty.modules.stock.application.dto.ReceiptResponse;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.aggregate.ReceiptAggregate;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.entity.ReceiptItem;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceiptStatus;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceptionType;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class ReceiptAssembler {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final Map<ReceptionType, String> TYPE_LABELS = Map.of(
            ReceptionType.CONSOMMABLE, "Consommable",
            ReceptionType.MATIERE_PREMIERE, "Matière première",
            ReceptionType.MATERIEL, "Matériel"
    );

    private static final Map<ReceiptStatus, String> STATUS_LABELS = Map.of(
            ReceiptStatus.PENDING_FIRST_VALIDATION, "En attente de 1ère validation",
            ReceiptStatus.PENDING_SECOND_VALIDATION, "En attente de 2nde validation",
            ReceiptStatus.VALIDATED, "Validée",
            ReceiptStatus.REJECTED, "Rejetée"
    );

    public ReceiptResponse toResponse(ReceiptAggregate a) {
        ReceiptResponse r = new ReceiptResponse();
        r.setId(a.getId());
        r.setReceiptNumber(a.getReceiptNumber().getValue());
        r.setReceptionType(a.getReceptionType().name());
        r.setReceptionTypeLabel(TYPE_LABELS.get(a.getReceptionType()));
        r.setSourceLabel(a.getSourceLabel());
        r.setSourceId(a.getSourceId());
        r.setReceiptDate(a.getReceiptDate() != null ? a.getReceiptDate().format(FMT) : null);
        r.setDestinationLocationId(a.getDestinationLocationId() != null ? a.getDestinationLocationId().toString() : null);
        r.setStatus(a.getStatus().name());
        r.setStatusLabel(STATUS_LABELS.get(a.getStatus()));
        r.setCreatedBy(a.getCreatedBy() != null ? a.getCreatedBy().getMatricule() : null);
        r.setRequiredFirstValidatorRole(a.getRequiredFirstValidatorRole());
        r.setRequiredSecondValidatorRole(a.getRequiredSecondValidatorRole());
        r.setFirstValidator(a.getFirstValidator() != null ? a.getFirstValidator().getMatricule() : null);
        r.setFirstValidatedAt(a.getFirstValidatedAt() != null ? a.getFirstValidatedAt().format(FMT) : null);
        r.setFirstValidationNotes(a.getFirstValidationNotes());
        r.setSecondValidator(a.getSecondValidator() != null ? a.getSecondValidator().getMatricule() : null);
        r.setSecondValidatedAt(a.getSecondValidatedAt() != null ? a.getSecondValidatedAt().format(FMT) : null);
        r.setSecondValidationNotes(a.getSecondValidationNotes());
        r.setRequiresAuthCode(a.isRequiresAuthCode());
        r.setRejectionReason(a.getRejectionReason());
        r.setRejectedBy(a.getRejectedBy() != null ? a.getRejectedBy().getMatricule() : null);
        r.setRejectedAt(a.getRejectedAt() != null ? a.getRejectedAt().format(FMT) : null);
        r.setItems(a.getItems().stream().map(this::toItemResponse).toList());
        return r;
    }

    private ReceiptItemResponse toItemResponse(ReceiptItem item) {
        ReceiptItemResponse ir = new ReceiptItemResponse();
        ir.setId(item.getId());
        ir.setProductId(item.getProductId().getValue());
        ir.setProductName(item.getProductName());
        ir.setProductSku(item.getProductSku());
        ir.setProductUnit(item.getOrderedQty().getUnit());
        ir.setPackagingType(item.getPackagingType());
        ir.setQuantityPerCarton(item.getQuantityPerCarton());
        ir.setOrderedQty(item.getOrderedQty().getValue());
        ir.setReceivedQty(item.getReceivedQty().getValue());
        ir.setDeviation(item.getDeviation());
        ir.setDeviationPercent(item.getDeviationPercent());
        ir.setExactMatch(item.isExactMatch());
        ir.setDeviationReason(item.getDeviationReason());
        ir.setLotNumber(item.getLotNumber());
        return ir;
    }
}
