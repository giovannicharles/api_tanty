package com.NTFOODS.Api_tanty.modules.stock.domain.reception.entity;

import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.Quantity;

import java.math.BigDecimal;

/**
 * ReceiptItem - Ligne de réception : produit commandé/attendu vs réellement reçu.
 * Porte les informations de conditionnement précises exigées par le cahier des charges
 * NT Foods (§2 Gestion des Conditionnements) : type d'emballage, quantité par carton,
 * numéro de lot. Volontairement dénormalisé (nom produit, SKU, unité recopiés à la
 * réception) pour refléter exactement ce qui est physiquement inscrit sur le bon de
 * livraison, indépendamment de l'état courant de la fiche produit.
 *
 * Aucune information financière ici (prix, valorisation) : conforme au principe
 * d'isolation financière du module Stock - seules les quantités physiques et statuts
 * de mouvement sont exposés, la valorisation appartient exclusivement au module
 * Comptabilité.
 */
public class ReceiptItem {
    private Long id;
    private final ProductId productId;
    private final String productName;
    private final String productSku;
    private final String packagingType;         // SACHET, ETUI, SEAU, CARTON, BIDON, BOUTEILLE...
    private final BigDecimal quantityPerCarton;  // Quantité par carton/conditionnement (peut être null)
    private final Quantity orderedQty;           // Quantité attendue (bon de commande / bon de production)
    private Quantity receivedQty;                // Quantité réellement reçue et contrôlée
    private String lotNumber;
    private String deviationReason;

    public ReceiptItem(ProductId productId, String productName, String productSku,
                        String packagingType, BigDecimal quantityPerCarton,
                        Quantity orderedQty, String lotNumber) {
        if (productName == null || productName.isBlank())
            throw new IllegalArgumentException("Le nom du produit est requis");
        if (productSku == null || productSku.isBlank())
            throw new IllegalArgumentException("Le SKU du produit est requis");
        this.productId = productId;
        this.productName = productName;
        this.productSku = productSku;
        this.packagingType = packagingType;
        this.quantityPerCarton = quantityPerCarton;
        this.orderedQty = orderedQty;
        this.receivedQty = new Quantity(BigDecimal.ZERO, orderedQty.getUnit());
        this.lotNumber = lotNumber;
    }

    /** Constructeur d'hydratation depuis la persistance (ne redéclenche aucune règle métier). */
    public static ReceiptItem hydrate(Long id, ProductId productId, String productName, String productSku,
                                       String packagingType, BigDecimal quantityPerCarton,
                                       Quantity orderedQty, Quantity receivedQty,
                                       String lotNumber, String deviationReason) {
        ReceiptItem item = new ReceiptItem(productId, productName, productSku, packagingType,
                quantityPerCarton, orderedQty, lotNumber);
        item.id = id;
        item.receivedQty = receivedQty;
        item.deviationReason = deviationReason;
        return item;
    }

    public void recordReceivedQuantity(Quantity receivedQty, String reason) {
        if (!receivedQty.getUnit().equals(orderedQty.getUnit()))
            throw new IllegalArgumentException("Unité incompatible avec la quantité attendue");
        this.receivedQty = receivedQty;
        this.deviationReason = isExactMatch() ? null : reason;
    }

    public boolean isExactMatch() {
        return orderedQty.getValue().compareTo(receivedQty.getValue()) == 0;
    }

    /** Écart signé (reçu - attendu). Positif = surplus, négatif = manquant. */
    public BigDecimal getDeviation() {
        return receivedQty.getValue().subtract(orderedQty.getValue());
    }

    /** Écart en pourcentage de la quantité attendue (valeur absolue), 0 si rien n'était attendu. */
    public BigDecimal getDeviationPercent() {
        if (orderedQty.getValue().compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return getDeviation().abs()
                .divide(orderedQty.getValue(), 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    // Getters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ProductId getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getProductSku() { return productSku; }
    public String getPackagingType() { return packagingType; }
    public BigDecimal getQuantityPerCarton() { return quantityPerCarton; }
    public Quantity getOrderedQty() { return orderedQty; }
    public Quantity getReceivedQty() { return receivedQty; }
    public String getLotNumber() { return lotNumber; }
    public String getDeviationReason() { return deviationReason; }
}
