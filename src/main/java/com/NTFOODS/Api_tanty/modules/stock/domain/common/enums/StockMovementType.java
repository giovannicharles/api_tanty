package com.NTFOODS.Api_tanty.modules.stock.domain.common.enums;

/**
 * StockMovementType - Type de mouvement de stock
 */
public enum StockMovementType {
    
    /**
     * RECEPTION_PRODUCTION - Réception des produits finis depuis la production
     */
    RECEPTION_PRODUCTION,
    
    /**
     * RECEPTION_CONSOMMABLE - Réception des consommables
     */
    RECEPTION_CONSOMMABLE,
    
    /**
     * RECEPTION_RAW_MATERIAL - Réception des matières premières
     */
    RECEPTION_RAW_MATERIAL,

    /**
     * RECEPTION_MATERIEL - Réception des matériels et équipements
     */
    RECEPTION_MATERIEL,

    /**
     * TRANSFER_CENTRAL_TO_BUFFER - Transfert du stock central vers le tampon
     */
    TRANSFER_CENTRAL_TO_BUFFER,
    
    /**
     * TRANSFER_BUFFER_TO_MOBILE - Transfert du tampon vers le stock mobile (dotation)
     */
    TRANSFER_BUFFER_TO_MOBILE,
    
    /**
     * TRANSFER_MOBILE_TO_CENTRAL - Retour du stock mobile vers le stock central
     */
    TRANSFER_MOBILE_TO_CENTRAL,
    
    /**
     * SALE - Vente directe depuis le stock
     */
    SALE,
    
    /**
     * ADJUSTMENT - Ajustement d'inventaire (positif ou négatif)
     */
    ADJUSTMENT,
    
    /**
     * LOSS - Perte ou casse
     */
    LOSS,
    
    /**
     * EXPIRATION - Produit expiré
     */
    EXPIRATION
}
