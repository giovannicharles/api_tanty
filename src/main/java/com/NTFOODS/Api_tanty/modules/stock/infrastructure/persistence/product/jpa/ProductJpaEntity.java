package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
public class ProductJpaEntity {
  @Id
  private Long id;
  private String sku;
  private Long variantId;
  private String barcode;
  private String category;
  private String unit;
  private BigDecimal unitPriceAmount;
  private Integer leadTimeDays;
  private Integer safetyStockDays;
  private boolean active;

  // Conditionnement fields
  private String packagingType; // SACHET, ETUI, SEAU, BOUTEILLE, DOYPACK, BOITE
  private Integer quantityPerCarton; // Number of units per carton
  private BigDecimal unitWeight; // Weight per unit in grams
  private String volume; // Volume for liquids (e.g., "1L", "2L", "5L")
  private Integer cartonsPerAssortiment; // For assorti cartons

  /**
   * materialType - Classification fondamentale du produit pour le routage des
   * réceptions : MATIERE_PREMIERE / CONSOMMABLE / PRODUIT_FINI (cf. ReceptionType).
   * Distinct de `category`, qui décrit la gamme commerciale (Bouillies, Custard...)
   * pour les produits finis uniquement. Ajouté avec une valeur par défaut pour ne
   * pas casser les 40 produits déjà seedés (tous des PRODUIT_FINI) : le constructeur
   * historique à 14 arguments reste utilisable tel quel.
   */
  private String materialType = "PRODUIT_FINI";

  /** Constructeur historique (produits finis, conservé pour compatibilité avec le seeder existant). */
  public ProductJpaEntity(Long id, String sku, Long variantId, String barcode, String category, String unit,
                           BigDecimal unitPriceAmount, Integer leadTimeDays, Integer safetyStockDays, boolean active,
                           String packagingType, Integer quantityPerCarton, BigDecimal unitWeight, String volume,
                           Integer cartonsPerAssortiment) {
    this.id = id;
    this.sku = sku;
    this.variantId = variantId;
    this.barcode = barcode;
    this.category = category;
    this.unit = unit;
    this.unitPriceAmount = unitPriceAmount;
    this.leadTimeDays = leadTimeDays;
    this.safetyStockDays = safetyStockDays;
    this.active = active;
    this.packagingType = packagingType;
    this.quantityPerCarton = quantityPerCarton;
    this.unitWeight = unitWeight;
    this.volume = volume;
    this.cartonsPerAssortiment = cartonsPerAssortiment;
    this.materialType = "PRODUIT_FINI";
  }

  /** Constructeur complet, avec materialType explicite (matières premières / consommables). */
  public ProductJpaEntity(Long id, String sku, Long variantId, String barcode, String category, String unit,
                           BigDecimal unitPriceAmount, Integer leadTimeDays, Integer safetyStockDays, boolean active,
                           String packagingType, Integer quantityPerCarton, BigDecimal unitWeight, String volume,
                           Integer cartonsPerAssortiment, String materialType) {
    this(id, sku, variantId, barcode, category, unit, unitPriceAmount, leadTimeDays, safetyStockDays, active,
         packagingType, quantityPerCarton, unitWeight, volume, cartonsPerAssortiment);
    this.materialType = materialType;
  }

  // getters/setters
}
