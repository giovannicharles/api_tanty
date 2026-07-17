package com.NTFOODS.Api_tanty.modules.stock.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * StockItemResponse - Vue enrichie d'un item de stock (Stock Central/Tampon/Mobile).
 * Avant cette classe, l'API renvoyait l'entité domaine StockItem brute, qui ne porte
 * que productId/productSku : aucun nom de produit, aucune unité, aucune catégorie
 * n'était affichable côté frontend sans un second appel manuel au catalogue.
 */
public class StockItemResponse {
    private Long id;
    private String locationId;
    private Long productId;
    private String productSku;
    private String productName;
    private String productUnit;
    private String productCategory;
    private String materialType;
    private String productLineName;
    private String brandName;
    private String packagingType;
    private BigDecimal quantity;
    private BigDecimal quantityPerCarton;
    private BigDecimal cartons;
    private BigDecimal unitWeight;
    private String volume;
    private Integer cartonsPerAssortiment;
    private BigDecimal reorderPoint;
    private BigDecimal safetyStock;
    private LocalDateTime lastUpdated;
    private String lastUpdatedBy;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLocationId() { return locationId; }
    public void setLocationId(String locationId) { this.locationId = locationId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductUnit() { return productUnit; }
    public void setProductUnit(String productUnit) { this.productUnit = productUnit; }
    public String getProductCategory() { return productCategory; }
    public void setProductCategory(String productCategory) { this.productCategory = productCategory; }
    public String getMaterialType() { return materialType; }
    public void setMaterialType(String materialType) { this.materialType = materialType; }
    public String getProductLineName() { return productLineName; }
    public void setProductLineName(String productLineName) { this.productLineName = productLineName; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public String getPackagingType() { return packagingType; }
    public void setPackagingType(String packagingType) { this.packagingType = packagingType; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getQuantityPerCarton() { return quantityPerCarton; }
    public void setQuantityPerCarton(BigDecimal quantityPerCarton) { this.quantityPerCarton = quantityPerCarton; }
    public BigDecimal getCartons() { return cartons; }
    public void setCartons(BigDecimal cartons) { this.cartons = cartons; }
    public BigDecimal getUnitWeight() { return unitWeight; }
    public void setUnitWeight(BigDecimal unitWeight) { this.unitWeight = unitWeight; }
    public String getVolume() { return volume; }
    public void setVolume(String volume) { this.volume = volume; }
    public Integer getCartonsPerAssortiment() { return cartonsPerAssortiment; }
    public void setCartonsPerAssortiment(Integer cartonsPerAssortiment) { this.cartonsPerAssortiment = cartonsPerAssortiment; }
    public BigDecimal getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(BigDecimal reorderPoint) { this.reorderPoint = reorderPoint; }
    public BigDecimal getSafetyStock() { return safetyStock; }
    public void setSafetyStock(BigDecimal safetyStock) { this.safetyStock = safetyStock; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    public String getLastUpdatedBy() { return lastUpdatedBy; }
    public void setLastUpdatedBy(String lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }
}
