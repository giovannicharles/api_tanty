package com.NTFOODS.Api_tanty.modules.stock.domain.product.entity;

/**
 * ProductClassification - Classification détaillée des produits
 * Gère la marque, la gamme, la variété et le conditionnement
 */
public class ProductClassification {
    
    private Long id;
    private final String brand;        // Marque (ex: TANTY, TANTYA)
    private final String range;        // Gamme (ex: Bouillies, Grignoter, Ingrédients, Chocolat)
    private final String variety;      // Variété (ex: Arachide, Nature, Banane)
    private final String packaging;    // Conditionnement (ex: SACHET, ETUI, SEAU, CARTON)
    private final String packagingDetails; // Détails du conditionnement (ex: "62g", "200g", "2L")
    private final Integer quantityPerCarton; // Quantité par carton
    private final String unitWeight;  // Poids unitaire (ex: "62g", "200g")
    private final String volume;      // Volume (ex: "1L", "500mL")
    private final Integer cartonsPerAssortiment; // Nombre de cartons par assortiment
    
    public ProductClassification(String brand, String range, String variety, String packaging,
                               String packagingDetails, Integer quantityPerCarton, 
                               String unitWeight, String volume, Integer cartonsPerAssortiment) {
        this.brand = brand;
        this.range = range;
        this.variety = variety;
        this.packaging = packaging;
        this.packagingDetails = packagingDetails;
        this.quantityPerCarton = quantityPerCarton;
        this.unitWeight = unitWeight;
        this.volume = volume;
        this.cartonsPerAssortiment = cartonsPerAssortiment;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public String getRange() {
        return range;
    }
    
    public String getVariety() {
        return variety;
    }
    
    public String getPackaging() {
        return packaging;
    }
    
    public String getPackagingDetails() {
        return packagingDetails;
    }
    
    public Integer getQuantityPerCarton() {
        return quantityPerCarton;
    }
    
    public String getUnitWeight() {
        return unitWeight;
    }
    
    public String getVolume() {
        return volume;
    }
    
    public Integer getCartonsPerAssortiment() {
        return cartonsPerAssortiment;
    }
    
    /**
     * Génère un code de classification unique
     * Format: BRAND-RANGE-VARIETY-PACKAGING
     */
    public String generateClassificationCode() {
        return String.format("%s-%s-%s-%s", 
                brand.toUpperCase(), 
                range.toUpperCase(), 
                variety.toUpperCase(), 
                packaging.toUpperCase());
    }
    
    /**
     * Génère une description lisible
     */
    public String generateDescription() {
        return String.format("%s %s %s - %s %s", 
                brand, range, variety, packagingDetails, packaging);
    }
    
    /**
     * PackagingType - Types de conditionnement disponibles
     */
    public enum PackagingType {
        SACHET("Sachet"),
        ETUI("Étui"),
        SEAU("Seau"),
        CARTON("Carton"),
        BIDON("Bidon"),
        BOUTEILLE("Bouteille"),
        POT("Pot"),
        SAC("Sac"),
        CAISSE("Caisse"),
        PALETTE("Palette");
        
        private final String displayName;
        
        PackagingType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * RangeType - Types de gammes disponibles
     */
    public enum RangeType {
        BOUILLIES("Bouillies"),
        GRIGNOTER("Grignoter"),
        INGREDIENTS("Ingrédients"),
        CHOCOLAT("Chocolat"),
        BOISSONS("Boissons"),
        SNACKS("Snacks"),
        DESSERTS("Desserts");
        
        private final String displayName;
        
        RangeType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
