package com.NTFOODS.Api_tanty.modules.stock.application.dto;

import java.util.List;

public class CreateReceiptRequest {
    private String receptionType;      // CONSOMMABLE | MATIERE_PREMIERE | MATERIEL
    private String sourceLabel;        // Nom fournisseur ou référence commande interne/production
    private Long sourceId;
    private String destinationLocationId;
    private String receiptDate;        // ISO-8601, optionnel (défaut: maintenant)
    private List<ReceiptItemRequest> items;

    public String getReceptionType() { return receptionType; }
    public void setReceptionType(String receptionType) { this.receptionType = receptionType; }
    public String getSourceLabel() { return sourceLabel; }
    public void setSourceLabel(String sourceLabel) { this.sourceLabel = sourceLabel; }
    public Long getSourceId() { return sourceId; }
    public void setSourceId(Long sourceId) { this.sourceId = sourceId; }
    public String getDestinationLocationId() { return destinationLocationId; }
    public void setDestinationLocationId(String destinationLocationId) { this.destinationLocationId = destinationLocationId; }
    public String getReceiptDate() { return receiptDate; }
    public void setReceiptDate(String receiptDate) { this.receiptDate = receiptDate; }
    public List<ReceiptItemRequest> getItems() { return items; }
    public void setItems(List<ReceiptItemRequest> items) { this.items = items; }
}
