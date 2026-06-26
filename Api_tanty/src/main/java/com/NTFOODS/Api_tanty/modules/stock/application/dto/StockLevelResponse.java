package com.NTFOODS.Api_tanty.modules.stock.application.dto;

import java.math.BigDecimal;

/**
 * StockLevelResponse - DTO pour les niveaux de stock
 * Contient les informations sur le niveau de stock d'un produit
 */
public class StockLevelResponse {

    // Identifiant du niveau de stock
    private Long id;

    // Nom du produit
    private String productName;

    // SKU du produit
    private String productSku;

    // Quantité actuelle en stock
    private Integer quantity;

    // Seuil de réapprovisionnement
    private Integer reorderPoint;

    // Valeur du stock
    private BigDecimal stockValue;

    // Identifiant de l'entrepôt
    private Integer warehouseId;

    // Nom de l'entrepôt
    private String warehouseName;

    // Niveau d'alerte (CRITIQUE, FAIBLE, NORMAL, SURPLUS)
    private String alertLevel;

    /**
     * Constructeur par défaut
     */
    public StockLevelResponse() {
    }

    /**
     * Constructeur complet
     * @param id Identifiant
     * @param productName Nom du produit
     * @param productSku SKU du produit
     * @param quantity Quantité
     * @param reorderPoint Seuil de réapprovisionnement
     * @param stockValue Valeur du stock
     * @param warehouseId Identifiant de l'entrepôt
     * @param warehouseName Nom de l'entrepôt
     * @param alertLevel Niveau d'alerte
     */
    public StockLevelResponse(
            Long id,
            String productName,
            String productSku,
            Integer quantity,
            Integer reorderPoint,
            BigDecimal stockValue,
            Integer warehouseId,
            String warehouseName,
            String alertLevel
    ) {
        this.id = id;
        this.productName = productName;
        this.productSku = productSku;
        this.quantity = quantity;
        this.reorderPoint = reorderPoint;
        this.stockValue = stockValue;
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.alertLevel = alertLevel;
    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getReorderPoint() {
        return reorderPoint;
    }

    public void setReorderPoint(Integer reorderPoint) {
        this.reorderPoint = reorderPoint;
    }

    public BigDecimal getStockValue() {
        return stockValue;
    }

    public void setStockValue(BigDecimal stockValue) {
        this.stockValue = stockValue;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(String alertLevel) {
        this.alertLevel = alertLevel;
    }
}
