package com.NTFOODS.Api_tanty.modules.controle.presentation.controller;

import com.NTFOODS.Api_tanty.shared.infrastructure.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

@RestController
@RequestMapping("/api/controle")
@Tag(name = "Contrôle — Tableau de bord", description = "KPIs et analyses pour le Contrôleur Général")
@PreAuthorize("hasAnyAuthority('ROLE_VALIDATEUR','ROLE_DIRECTION','ROLE_ADMIN')")
public class ControleController {

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> dashboard() {
        Map<String, Object> data = Map.ofEntries(
            entry("caTotal", 0), entry("margeGlobale", 0), entry("tauxMarge", 0),
            entry("nbAnomalies", 0), entry("nbValidationsEnAttente", 0),
            entry("budgetConsomme", 0), entry("budgetTotal", 0)
        );
        return ResponseEntity.ok(ApiResponse.succes(data, "Dashboard Contrôle"));
    }

    @GetMapping("/marges")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> marges() {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 marge(s)"));
    }

    @GetMapping("/budget")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> budget() {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 poste(s) budgétaire(s)"));
    }

    @GetMapping("/variances")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> variances() {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 variance(s)"));
    }
}
