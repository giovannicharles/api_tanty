package com.NTFOODS.Api_tanty.modules.stock.application.reception.handler;

import com.NTFOODS.Api_tanty.modules.stock.application.dto.CreateReceiptRequest;
import com.NTFOODS.Api_tanty.modules.stock.application.dto.ReceiptItemRequest;
import com.NTFOODS.Api_tanty.modules.stock.application.reception.ReceptionNotifier;
import com.NTFOODS.Api_tanty.modules.stock.application.reception.command.CreateReceiptCommand;
import com.NTFOODS.Api_tanty.modules.stock.application.service.ReceiptNumberGenerator;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.aggregate.ReceiptAggregate;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.entity.ReceiptItem;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceptionType;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.repository.ReceiptRepository;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject.ReceiptNumber;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * CreateReceiptHandler - Traite la création d'une réception.
 *
 * Réécrit entièrement : l'ancienne version appelait repository.save(receipt) qui
 * était un no-op complet (voir ReceiptRepositoryImpl), si bien qu'aucune réception
 * n'était jamais réellement créée malgré une réponse HTTP 200 trompeuse côté frontend.
 */
@Service
@Transactional
public class CreateReceiptHandler {

    private final ReceiptRepository receiptRepository;
    private final ReceiptNumberGenerator numberGenerator;
    private final ReceptionNotifier notifier;

    public CreateReceiptHandler(ReceiptRepository receiptRepository, ReceiptNumberGenerator numberGenerator,
                                 ReceptionNotifier notifier) {
        this.receiptRepository = receiptRepository;
        this.numberGenerator = numberGenerator;
        this.notifier = notifier;
    }

    public ReceiptAggregate handle(CreateReceiptCommand command) {
        CreateReceiptRequest req = command.getRequest();

        if (req.getReceptionType() == null || req.getReceptionType().isBlank())
            throw new IllegalArgumentException("Le type de réception est requis (CONSOMMABLE, MATIERE_PREMIERE ou MATERIEL)");
        ReceptionType type = ReceptionType.valueOf(req.getReceptionType());

        if (req.getDestinationLocationId() == null || req.getDestinationLocationId().isBlank())
            throw new IllegalArgumentException("L'emplacement de stock de destination est requis");
        UUID destinationLocationId = UUID.fromString(req.getDestinationLocationId());

        if (req.getItems() == null || req.getItems().isEmpty())
            throw new IllegalArgumentException("Au moins un article est requis");

        LocalDateTime receiptDate = (req.getReceiptDate() != null && !req.getReceiptDate().isBlank())
                ? LocalDateTime.parse(req.getReceiptDate())
                : LocalDateTime.now();

        List<ReceiptItem> items = new ArrayList<>();
        Map<ProductId, Quantity> receivedByProduct = new HashMap<>();
        Map<ProductId, String> reasonsByProduct = new HashMap<>();

        for (ReceiptItemRequest itemReq : req.getItems()) {
            if (itemReq.getProductId() == null)
                throw new IllegalArgumentException("Le produit est requis pour chaque ligne de réception");
            if (itemReq.getOrderedQty() == null || itemReq.getOrderedQty().compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("La quantité attendue est requise pour " + itemReq.getProductName());
            String unit = itemReq.getProductUnit() != null ? itemReq.getProductUnit() : "unite";

            ProductId productId = new ProductId(itemReq.getProductId());
            ReceiptItem item = new ReceiptItem(
                    productId,
                    itemReq.getProductName(),
                    itemReq.getProductSku(),
                    itemReq.getPackagingType(),
                    itemReq.getQuantityPerCarton(),
                    new Quantity(itemReq.getOrderedQty(), unit),
                    itemReq.getLotNumber()
            );
            items.add(item);

            if (itemReq.getReceivedQty() != null) {
                receivedByProduct.put(productId, new Quantity(itemReq.getReceivedQty(), unit));
                reasonsByProduct.put(productId, itemReq.getDeviationReason());
            }
        }

        ReceiptNumber receiptNumber = numberGenerator.generate(type);
        UserId createdBy = new UserId(command.getRequestedByMatricule());

        ReceiptAggregate aggregate = ReceiptAggregate.create(
                receiptNumber, type, req.getSourceLabel(), req.getSourceId(), receiptDate,
                destinationLocationId, createdBy, items
        );

        if (!receivedByProduct.isEmpty()) {
            aggregate.recordReceivedQuantities(receivedByProduct, reasonsByProduct);
        }

        ReceiptAggregate saved = receiptRepository.save(aggregate);

        notifier.notifyRole(saved.getRequiredFirstValidatorRole(),
                "Nouvelle réception à valider",
                "La réception " + saved.getReceiptNumber().getValue() + " (" + type + ") attend votre première validation.");

        return saved;
    }
}
