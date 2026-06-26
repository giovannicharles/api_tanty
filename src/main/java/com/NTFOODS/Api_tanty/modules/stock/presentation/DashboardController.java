package com.NTFOODS.Api_tanty.modules.stock.presentation;

import com.NTFOODS.Api_tanty.modules.stock.application.dto.DashboardStatsResponse;
import com.NTFOODS.Api_tanty.modules.stock.application.dto.StockLevelResponse;
import com.NTFOODS.Api_tanty.modules.stock.application.service.DashboardService;
import com.NTFOODS.Api_tanty.shared.infrastructure.dto.PageRequest;
import com.NTFOODS.Api_tanty.shared.infrastructure.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * DashboardController - Controller REST pour le dashboard du module Stock
 * Fournit les endpoints pour récupérer les statistiques et données du dashboard
 * Supporte la pagination pour les endpoints retournant des listes
 */
@RestController
@RequestMapping("/api/v1/stock/dashboard")
@Tag(name = "Stock Dashboard", description = "API pour le dashboard du module Stock - Statistiques et alertes de stock")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    // Service du dashboard pour la logique métier
    private final DashboardService dashboardService;

    /**
     * Constructeur avec injection du service dashboard
     * @param dashboardService Service dashboard
     */
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Endpoint pour récupérer les statistiques générales du dashboard
     * @return Réponse contenant les statistiques du dashboard
     */
    @GetMapping("/stats")
    @Operation(
        summary = "Récupérer les statistiques du dashboard",
        description = "Retourne les KPIs principaux du dashboard stock: nombre total de produits, valeur totale du stock, produits en alerte, etc."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Statistiques récupérées avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DashboardStatsResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non authentifié - Token JWT manquant ou invalide",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Accès refusé - Rôle insuffisant (nécessite ROLE_STOCK ou ROLE_ADMIN)",
            content = @Content
        )
    })
    @PreAuthorize("hasAuthority('ROLE_STOCK') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        // Appel au service pour récupérer les statistiques
        DashboardStatsResponse stats = dashboardService.getDashboardStats();

        // Retourner les statistiques avec un code HTTP 200
        return ResponseEntity.ok(stats);
    }

    /**
     * Endpoint pour récupérer les niveaux de stock en alerte avec pagination
     * @param page Numéro de la page (défaut: 0)
     * @param size Taille de la page (défaut: 10, max: 100)
     * @return Page des niveaux de stock en alerte
     */
    @GetMapping("/alerts")
    @Operation(
        summary = "Récupérer les alertes de stock",
        description = "Retourne les produits avec des alertes de stock (stock inférieur au seuil minimum). Supporte la pagination."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Alertes de stock récupérées avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PageResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non authentifié - Token JWT manquant ou invalide",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Accès refusé - Rôle insuffisant (nécessite ROLE_STOCK ou ROLE_ADMIN)",
            content = @Content
        )
    })
    @PreAuthorize("hasAuthority('ROLE_STOCK') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<PageResponse<StockLevelResponse>> getStockAlerts(
            @Parameter(description = "Numéro de la page (commence à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page (max: 100)", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        // Créer l'objet de pagination
        PageRequest pageRequest = new PageRequest(page, size);

        // Appel au service pour récupérer les alertes paginées
        PageResponse<StockLevelResponse> alerts = dashboardService.getStockAlerts(pageRequest);

        // Retourner les alertes avec un code HTTP 200
        return ResponseEntity.ok(alerts);
    }

    /**
     * Endpoint pour récupérer les niveaux de stock avec pagination
     * @param page Numéro de la page (défaut: 0)
     * @param size Taille de la page (défaut: 10, max: 100)
     * @return Page des niveaux de stock
     */
    @GetMapping("/stock-levels")
    @Operation(
        summary = "Récupérer les niveaux de stock",
        description = "Retourne tous les niveaux de stock avec pagination. Inclut les informations sur la quantité, le seuil minimum et le statut."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Niveaux de stock récupérés avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PageResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non authentifié - Token JWT manquant ou invalide",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Accès refusé - Rôle insuffisant (nécessite ROLE_STOCK ou ROLE_ADMIN)",
            content = @Content
        )
    })
    @PreAuthorize("hasAuthority('ROLE_STOCK') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<PageResponse<StockLevelResponse>> getStockLevels(
            @Parameter(description = "Numéro de la page (commence à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page (max: 100)", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        // Créer l'objet de pagination
        PageRequest pageRequest = new PageRequest(page, size);

        // Appel au service pour récupérer les niveaux de stock paginés
        PageResponse<StockLevelResponse> stockLevels = dashboardService.getStockLevels(pageRequest);

        // Retourner les niveaux de stock avec un code HTTP 200
        return ResponseEntity.ok(stockLevels);
    }
}
