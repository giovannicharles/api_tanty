package com.NTFOODS.Api_tanty.modules.stock.domain.common.enums;

/**
 * WarehouseType - Type de magasin/entrepôt dans l'ERP TANTY
 * Définit les différents types de magasins pour la gestion des stocks
 */
public enum WarehouseType {
    
    /**
     * Magasin de matières premières
     * Stocke les ingrédients et composants bruts pour la production
     */
    RAW_MATERIALS("Matières Premières"),
    
    /**
     * Magasin de produits finis
     * Stocke les produits prêts à être vendus
     */
    FINISHED_PRODUCTS("Produits Finis"),
    
    /**
     * Magasin de matériels
     * Stocke les équipements, outils et matériels de l'entreprise
     */
    EQUIPMENT("Matériels"),
    
    /**
     * Magasin de produits semi-finis
     * Stocke les produits en cours de transformation
     */
    SEMI_FINISHED("Produits Semi-Finis"),
    
    /**
     * Magasin de consommables
     * Stocke les articles consommables (emballages, étiquettes, etc.)
     */
    CONSUMABLES("Consommables"),
    
    /**
     * Magasin central
     * Magasin principal servant de stock centralisé
     */
    CENTRAL("Magasin Central");
    
    private final String displayName;
    
    WarehouseType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
