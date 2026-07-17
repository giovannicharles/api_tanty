package com.NTFOODS.Api_tanty.modules.stock.domain.common.enums;

/**
 * StockLocationType - Type de localisation de stock
 * Définit les différents niveaux de stock dans l'ERP TANTY
 */
public enum StockLocationType {
    
    /**
     * STOCK_CENTRAL - Stock principal de l'entreprise
     * Alimenté par la production, alimente le tampon
     */
    STOCK_CENTRAL,
    
    /**
     * STOCK_BUFFER - Tampon (Buffer)
     * Stock intermédiaire pour les sorties commerciales
     * Alimenté par le stock central, utilisé pour les dotations
     */
    STOCK_BUFFER,
    
    /**
     * STOCK_MOBILE - Stock mobile des commerciaux
     * Stock détenu par les commerciaux sur le terrain
     * Alimenté par le tampon via dotations
     */
    STOCK_MOBILE,

    /**
     * MAGASIN - Magasin géré par un gestionnaire de stock
     * Un gestionnaire peut gérer plusieurs magasins
     * Chaque magasin a son propre stock, adresse et contact
     */
    MAGASIN
}
