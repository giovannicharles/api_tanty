package com.NTFOODS.Api_tanty.modules.stock.application.reception.handler;

import com.NTFOODS.Api_tanty.modules.stock.application.reception.command.CreateReceiptCommand;
import com.NTFOODS.Api_tanty.modules.stock.application.service.ReceiptNumberGenerator;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.aggregate.ReceiptAggregate;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.entity.ReceiptItem;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.repository.ReceiptRepository;

import com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject.ReceiptNumber;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CreateReceiptHandler - Handler pour la création de réceptions
 * Gère la création de réceptions fournisseurs et de production
 */
@Component

public class CreateReceiptHandler {

    @Autowired
    private ReceiptRepository receiptRepository;
    @Autowired
    private ReceiptNumberGenerator receiptNumberGenerator;


  /**
     * Gère la commande de création de réception
     * @param cmd Commande de création de réception
     * @return ReceiptNumber de la réception créée
     */
    public ReceiptNumber handle(CreateReceiptCommand cmd) {
        // Générer un numéro de réception unique
        ReceiptNumber receiptNumber = receiptNumberGenerator.generate();

        // Convertir les DTOs en entités ReceiptItem
        List<ReceiptItem> items = cmd.items().stream()
                .map(item -> new ReceiptItem(item.productId(), item.orderedQty()))
                .collect(Collectors.toList());

        // Créer l'agrégat de réception
        ReceiptAggregate receipt = ReceiptAggregate.create(
                receiptNumber,
                cmd.source(),
                cmd.sourceId(),
                cmd.receiptDate(),
                cmd.warehouseId(),
                items
        );

        // Sauvegarder la réception
        receiptRepository.save(receipt);

        return receiptNumber;
    }
}
