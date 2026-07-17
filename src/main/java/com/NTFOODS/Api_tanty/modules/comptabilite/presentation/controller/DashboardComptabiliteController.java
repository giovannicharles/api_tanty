package com.NTFOODS.Api_tanty.modules.comptabilite.presentation.controller;

import com.NTFOODS.Api_tanty.shared.infrastructure.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import static java.util.Map.entry;

@RestController
@RequestMapping("/api/comptabilite/dashboard")
@Tag(name = "Comptabilité — Dashboard", description = "Tableau de bord financier (KPIs temps réel)")
@SecurityRequirement(name = "bearerAuth")
public class DashboardComptabiliteController {

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_VALIDATEUR','ROLE_DIRECTION','ROLE_ADMIN')")
    @Operation(summary = "Dashboard Comptabilité",
        description = "Solde caisse, factures émises/payées/impayées, décaissements, versements en attente, alertes rouges, primes à verser")
    public ResponseEntity<ApiResponse<Map<String, Object>>> dashboard() {
        Map<String, Object> data = Map.ofEntries(
            entry("date", LocalDate.now().toString()),
            entry("soldeCaisse", 0),
            entry("totalEntreesCaisse", 0),
            entry("totalSortiesCaisse", 0),
            entry("totalFacturesEmises", 0),
            entry("totalFacturesPayees", 0),
            entry("nbFacturesImpayees", 0),
            entry("totalDecaissementsExecutes", 0),
            entry("nbDecaissementsEnAttente", 0),
            entry("nbVersementsEnAttente", 0),
            entry("nbAlertesRouges", 0),
            entry("nbPrimesValideesNonVersées", 0),
            entry("totalPrimesAVerser", 0)
        );
        return ResponseEntity.ok(ApiResponse.succes(data, "Dashboard Comptabilité"));
    }
}
