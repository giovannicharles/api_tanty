package com.NTFOODS.Api_tanty.modules.stock.application.dto;

/**
 * ProductCatalogResponse - DTO du catalogue produit exposé par le module Stock.
 * Volontairement dépourvu de tout champ de prix/valorisation : conforme au principe
 * d'isolation financière (le Stock n'expose que des informations physiques ;
 * la valorisation appartient exclusivement au module Comptabilité). L'entité
 * ProductJpaEntity porte encore un champ unitPriceAmount - un point de vigilance
 * architectural signalé séparément - mais ce DTO ne le republie pas.
 */
public class ProductCatalogResponse {
    private Long id;
    private String sku;
    private String barcode;
    private String category;
    private String unit;
    private boolean active;
    private String designation;
    private String packagingType;
    private Integer quantityPerCarton;
    private String volume;
    private String brandName;
    private String productLineName;
    private String variantName;
    private String materialType;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public String getPackagingType() { return packagingType; }
    public void setPackagingType(String packagingType) { this.packagingType = packagingType; }
    public Integer getQuantityPerCarton() { return quantityPerCarton; }
    public void setQuantityPerCarton(Integer quantityPerCarton) { this.quantityPerCarton = quantityPerCarton; }
    public String getVolume() { return volume; }
    public void setVolume(String volume) { this.volume = volume; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public String getProductLineName() { return productLineName; }
    public void setProductLineName(String productLineName) { this.productLineName = productLineName; }
    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }
    public String getMaterialType() { return materialType; }
    public void setMaterialType(String materialType) { this.materialType = materialType; }
}
