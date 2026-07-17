package com.NTFOODS.Api_tanty.modules.stock.application.reception.command;

import com.NTFOODS.Api_tanty.modules.stock.application.dto.CreateReceiptRequest;

/** Commande de création d'une réception - encapsule la requête + l'auteur authentifié. */
public class CreateReceiptCommand {
    private final CreateReceiptRequest request;
    private final String requestedByMatricule;

    public CreateReceiptCommand(CreateReceiptRequest request, String requestedByMatricule) {
        this.request = request;
        this.requestedByMatricule = requestedByMatricule;
    }

    public CreateReceiptRequest getRequest() { return request; }
    public String getRequestedByMatricule() { return requestedByMatricule; }
}
