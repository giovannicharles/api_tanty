package com.NTFOODS.Api_tanty.modules.dg.presentation.controller;

import com.NTFOODS.Api_tanty.shared.infrastructure.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

@RestController
@RequestMapping("/api/dg")
@Tag(name = "DG — Tableau de bord", description = "KPIs et vues agrégées pour le Directeur Général")
@PreAuthorize("hasAnyAuthority('ROLE_DIRECTION','ROLE_ADMIN')")
public class DgController {

    @GetMapping("/kpis")
    public ResponseEntity<ApiResponse<Map<String, Object>>> kpis() {
        Map<String, Object> data = Map.ofEntries(
            entry("caTotal", 0), entry("margeNette", 0), entry("nbCommandes", 0),
            entry("nbClientsActifs", 0), entry("tauxRecouvrement", 0),
            entry("stockValeur", 0), entry("productionKg", 0), entry("nbEmployes", 0)
        );
        return ResponseEntity.ok(ApiResponse.succes(data, "KPIs DG"));
    }

    @GetMapping("/classement-commerciaux")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> classement() {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 commercial(s)"));
    }

    @GetMapping("/comparatif")
    public ResponseEntity<ApiResponse<Map<String, Object>>> comparatif(
        @RequestParam String periode, @RequestParam(required = false) String date) {
        Map<String, Object> data = Map.of(
            "periode", periode,
            "caActuel", 0, "caPrecedent", 0, "evolutionPct", 0
        );
        return ResponseEntity.ok(ApiResponse.succes(data, "Comparatif"));
    }

    @GetMapping("/taux-occupation-marches")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> tauxOccupation() {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 marché(s)"));
    }

    @GetMapping("/zones")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> zones() {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 zone(s)"));
    }

    @GetMapping("/validations")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> validations() {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 validation(s) en attente"));
    }

    @GetMapping("/anomalies")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> anomalies() {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 anomalie(s)"));
    }

    @GetMapping("/dashboard-stock")
    public ResponseEntity<ApiResponse<Map<String, Object>>> dashboardStock() {
        Map<String, Object> data = Map.of(
            "valeurTotale", 0, "nbProduits", 0, "nbAlertes", 0, "nbRuptures", 0
        );
        return ResponseEntity.ok(ApiResponse.succes(data, "Dashboard stock"));
    }

    @GetMapping("/dashboard-financier")
    public ResponseEntity<ApiResponse<Map<String, Object>>> dashboardFinancier() {
        Map<String, Object> data = Map.of(
            "soldeCaisse", 0, "creancesTotal", 0, "facturesImpayees", 0, "decaissementsEnAttente", 0
        );
        return ResponseEntity.ok(ApiResponse.succes(data, "Dashboard financier"));
    }

    @GetMapping("/dashboard-production")
    public ResponseEntity<ApiResponse<Map<String, Object>>> dashboardProduction() {
        Map<String, Object> data = Map.of(
            "productionJourKg", 0, "nbLots", 0, "tauxRendement", 0, "nbEmployesPresents", 0
        );
        return ResponseEntity.ok(ApiResponse.succes(data, "Dashboard production"));
    }
}
