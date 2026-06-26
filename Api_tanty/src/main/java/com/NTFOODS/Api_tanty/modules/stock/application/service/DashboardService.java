package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.application.dto.DashboardStatsResponse;
import com.NTFOODS.Api_tanty.modules.stock.application.dto.StockLevelResponse;
import com.NTFOODS.Api_tanty.shared.infrastructure.dto.PageRequest;
import com.NTFOODS.Api_tanty.shared.infrastructure.dto.PageResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DashboardService - Service pour la logique métier du dashboard Stock
 * Fournit les méthodes pour récupérer et calculer les statistiques du dashboard
 * Utilise le cache Redis pour optimiser les performances des requêtes fréquentes
 */
@Service
public class DashboardService {

    /**
     * Constructeur par défaut
     */
    public DashboardService() {
    }

    /**
     * Récupère les statistiques générales du dashboard
     * Calcule les KPIs principaux à partir des données de stock
     * Utilise le cache Redis pour éviter de recalculer ces statistiques à chaque requête
     * @return Réponse contenant les statistiques du dashboard
     */
    @Cacheable(value = "dashboardStats", key = "'stats'")
    public DashboardStatsResponse getDashboardStats() {
        // Pour l'instant, retourner des données simulées
        // À remplacer par les vraies données une fois les repositories implémentés

        // Nombre total de niveaux de stock (simulé)
        int totalStockLevels = 156;

        // Nombre de réceptions en attente (simulé)
        int pendingReceipts = 12;

        // Nombre de lots de production en attente (simulé)
        int pendingBatches = 8;

        // Nombre de commandes internes actives (simulé)
        int activeInternalOrders = 5;

        // Nombre d'alertes critiques (simulé)
        int criticalAlerts = 7;

        // Valeur totale du stock (simulé)
        BigDecimal totalStockValue = new BigDecimal("2450000.00");

        // Nombre d'entrepôts (fixe à 4 selon le frontend)
        int totalWarehouses = 4;

        // Construire et retourner la réponse des statistiques
        return new DashboardStatsResponse(
            totalStockLevels,
            pendingReceipts,
            pendingBatches,
            activeInternalOrders,
            criticalAlerts,
            totalStockValue,
            totalWarehouses
        );
    }

    /**
     * Récupère les niveaux de stock en alerte
     * Filtre les niveaux de stock avec un niveau d'alerte CRITIQUE ou FAIBLE
     * Utilise le cache Redis pour éviter de recalculer ces alertes à chaque requête
     * @param pageRequest Paramètres de pagination
     * @return Page des niveaux de stock en alerte
     */
    @Cacheable(value = "stockAlerts", key = "'alerts-' + #pageRequest.page + '-' + #pageRequest.size")
    public PageResponse<StockLevelResponse> getStockAlerts(PageRequest pageRequest) {
        // Pour l'instant, retourner des données simulées
        // À remplacer par les vraies données une fois les repositories implémentés
        List<StockLevelResponse> allAlerts = new ArrayList<>();

        // Ajouter quelques alertes simulées
        allAlerts.add(new StockLevelResponse(
            1L,
            "Farine de blé T55",
            "FAR-001",
            50,
            200,
            new BigDecimal("12500.00"),
            1,
            "Matières Premières",
            "CRITIQUE"
        ));

        allAlerts.add(new StockLevelResponse(
            2L,
            "Sucre cristallisé",
            "SUC-002",
            80,
            300,
            new BigDecimal("9600.00"),
            1,
            "Matières Premières",
            "CRITIQUE"
        ));

        allAlerts.add(new StockLevelResponse(
            3L,
            "Huile végétale",
            "HUI-003",
            150,
            250,
            new BigDecimal("18000.00"),
            1,
            "Matières Premières",
            "FAIBLE"
        ));

        // Appliquer la pagination
        int start = pageRequest.getOffset();
        int end = Math.min(start + pageRequest.getSize(), allAlerts.size());

        // Vérifier que start est valide
        if (start >= allAlerts.size()) {
            // Retourner une page vide si la page demandée dépasse le nombre total d'éléments
            return new PageResponse<>(new ArrayList<>(), pageRequest.getPage(), pageRequest.getSize(), allAlerts.size());
        }

        List<StockLevelResponse> pagedAlerts = allAlerts.subList(start, end);

        // Construire et retourner la réponse paginée
        return new PageResponse<>(pagedAlerts, pageRequest.getPage(), pageRequest.getSize(), allAlerts.size());
    }

    /**
     * Récupère tous les niveaux de stock
     * Utilise le cache Redis pour éviter de recalculer ces niveaux à chaque requête
     * @param pageRequest Paramètres de pagination
     * @return Page des niveaux de stock
     */
    @Cacheable(value = "stockLevels", key = "'levels-' + #pageRequest.page + '-' + #pageRequest.size")
    public PageResponse<StockLevelResponse> getStockLevels(PageRequest pageRequest) {
        // Pour l'instant, retourner des données simulées
        // À remplacer par les vraies données une fois les repositories implémentés
        List<StockLevelResponse> allStockLevels = new ArrayList<>();

        // Ajouter quelques niveaux de stock simulés
        allStockLevels.add(new StockLevelResponse(
            1L,
            "Farine de blé T55",
            "FAR-001",
            50,
            200,
            new BigDecimal("12500.00"),
            1,
            "Matières Premières",
            "CRITIQUE"
        ));

        allStockLevels.add(new StockLevelResponse(
            2L,
            "Sucre cristallisé",
            "SUC-002",
            80,
            300,
            new BigDecimal("9600.00"),
            1,
            "Matières Premières",
            "CRITIQUE"
        ));

        allStockLevels.add(new StockLevelResponse(
            3L,
            "Huile végétale",
            "HUI-003",
            150,
            250,
            new BigDecimal("18000.00"),
            1,
            "Matières Premières",
            "FAIBLE"
        ));

        allStockLevels.add(new StockLevelResponse(
            4L,
            "Levure sèche",
            "LEV-004",
            500,
            100,
            new BigDecimal("25000.00"),
            1,
            "Matières Premières",
            "NORMAL"
        ));

        allStockLevels.add(new StockLevelResponse(
            5L,
            "Pain complet",
            "PAN-001",
            200,
            50,
            new BigDecimal("1000.00"),
            3,
            "Produits Finis",
            "NORMAL"
        ));

        // Appliquer la pagination
        int start = pageRequest.getOffset();
        int end = Math.min(start + pageRequest.getSize(), allStockLevels.size());

        // Vérifier que start est valide
        if (start >= allStockLevels.size()) {
            // Retourner une page vide si la page demandée dépasse le nombre total d'éléments
            return new PageResponse<>(new ArrayList<>(), pageRequest.getPage(), pageRequest.getSize(), allStockLevels.size());
        }

        List<StockLevelResponse> pagedStockLevels = allStockLevels.subList(start, end);

        // Construire et retourner la réponse paginée
        return new PageResponse<>(pagedStockLevels, pageRequest.getPage(), pageRequest.getSize(), allStockLevels.size());
    }
}
