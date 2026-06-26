package com.NTFOODS.Api_tanty.modules.stock.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

/**
 * DashboardStatsResponse - DTO pour les statistiques du dashboard Stock
 * Contient les KPIs principaux affichés sur le dashboard
 */
@Schema(description = "Statistiques du dashboard Stock - KPIs principaux pour le monitoring")
public class DashboardStatsResponse {

    // Nombre total de niveaux de stock (articles)
    @Schema(
        description = "Nombre total d'articles/niveaux de stock dans le système",
        example = "1250"
    )
    private Integer totalStockLevels;

    // Nombre de réceptions en attente de validation
    @Schema(
        description = "Nombre de réceptions de marchandises en attente de validation",
        example = "15"
    )
    private Integer pendingReceipts;

    // Nombre de lots de production en attente
    @Schema(
        description = "Nombre de lots de production en attente de traitement",
        example = "8"
    )
    private Integer pendingBatches;

    // Nombre de commandes internes actives
    @Schema(
        description = "Nombre de commandes internes actuellement actives",
        example = "23"
    )
    private Integer activeInternalOrders;

    // Nombre d'alertes critiques
    @Schema(
        description = "Nombre d'alertes critiques (stock très bas, péremption proche, etc.)",
        example = "5"
    )
    private Integer criticalAlerts;

    // Valeur totale du stock
    @Schema(
        description = "Valeur totale du stock en euros",
        example = "1250000.50"
    )
    private BigDecimal totalStockValue;

    // Nombre d'entrepôts
    @Schema(
        description = "Nombre total d'entrepôts dans le système",
        example = "3"
    )
    private Integer totalWarehouses;

    /**
     * Constructeur par défaut
     */
    public DashboardStatsResponse() {
    }

    /**
     * Constructeur complet
     * @param totalStockLevels Nombre total de niveaux de stock
     * @param pendingReceipts Réceptions en attente
     * @param pendingBatches Lots en attente
     * @param activeInternalOrders Commandes actives
     * @param criticalAlerts Alertes critiques
     * @param totalStockValue Valeur totale du stock
     * @param totalWarehouses Nombre d'entrepôts
     */
    public DashboardStatsResponse(
            Integer totalStockLevels,
            Integer pendingReceipts,
            Integer pendingBatches,
            Integer activeInternalOrders,
            Integer criticalAlerts,
            BigDecimal totalStockValue,
            Integer totalWarehouses
    ) {
        this.totalStockLevels = totalStockLevels;
        this.pendingReceipts = pendingReceipts;
        this.pendingBatches = pendingBatches;
        this.activeInternalOrders = activeInternalOrders;
        this.criticalAlerts = criticalAlerts;
        this.totalStockValue = totalStockValue;
        this.totalWarehouses = totalWarehouses;
    }

    // Getters et Setters

    public Integer getTotalStockLevels() {
        return totalStockLevels;
    }

    public void setTotalStockLevels(Integer totalStockLevels) {
        this.totalStockLevels = totalStockLevels;
    }

    public Integer getPendingReceipts() {
        return pendingReceipts;
    }

    public void setPendingReceipts(Integer pendingReceipts) {
        this.pendingReceipts = pendingReceipts;
    }

    public Integer getPendingBatches() {
        return pendingBatches;
    }

    public void setPendingBatches(Integer pendingBatches) {
        this.pendingBatches = pendingBatches;
    }

    public Integer getActiveInternalOrders() {
        return activeInternalOrders;
    }

    public void setActiveInternalOrders(Integer activeInternalOrders) {
        this.activeInternalOrders = activeInternalOrders;
    }

    public Integer getCriticalAlerts() {
        return criticalAlerts;
    }

    public void setCriticalAlerts(Integer criticalAlerts) {
        this.criticalAlerts = criticalAlerts;
    }

    public BigDecimal getTotalStockValue() {
        return totalStockValue;
    }

    public void setTotalStockValue(BigDecimal totalStockValue) {
        this.totalStockValue = totalStockValue;
    }

    public Integer getTotalWarehouses() {
        return totalWarehouses;
    }

    public void setTotalWarehouses(Integer totalWarehouses) {
        this.totalWarehouses = totalWarehouses;
    }
}
