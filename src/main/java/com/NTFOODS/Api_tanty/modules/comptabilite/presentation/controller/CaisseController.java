package com.NTFOODS.Api_tanty.modules.comptabilite.presentation.controller;

import com.NTFOODS.Api_tanty.shared.infrastructure.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

@RestController
@RequestMapping("/api/comptabilite/caisse")
@Tag(name = "Comptabilité — Caisse", description = "Journal de caisse et sécurisation")
public class CaisseController {

    @GetMapping("/{date}")
    @PreAuthorize("hasAnyAuthority('ROLE_RH','ROLE_CAISSIER','ROLE_FINANCE','ROLE_VALIDATEUR','ROLE_DIRECTION','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCaisse(@PathVariable String date) {
        return ResponseEntity.ok(ApiResponse.succes(null, "Aucune caisse ouverte pour cette date"));
    }

    @PostMapping("/ouvrir")
    @PreAuthorize("hasAnyAuthority('ROLE_RH','ROLE_CAISSIER','ROLE_FINANCE','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> ouvrir(@RequestBody Map<String, Object> req) {
        double soldeInitial = req.get("soldeInitial") instanceof Number n ? n.doubleValue() : 0;
        String date = req.getOrDefault("date", LocalDate.now().toString()).toString();
        Map<String, Object> caisse = Map.ofEntries(
            entry("id", 1),
            entry("date", date),
            entry("soldeCourant", soldeInitial),
            entry("seuilSecurisation", 500000),
            entry("totalEntrees", 0),
            entry("totalSorties", 0),
            entry("lignes", List.of())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.succes(caisse, "Caisse ouverte"));
    }

    @PostMapping("/{date}/entree")
    @PreAuthorize("hasAnyAuthority('ROLE_RH','ROLE_CAISSIER','ROLE_FINANCE','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> entree(@PathVariable String date, @RequestBody Map<String, Object> req) {
        Map<String, Object> caisse = Map.of("date", date, "soldeCourant", 0, "lignes", List.of());
        return ResponseEntity.ok(ApiResponse.succes(caisse, "Entrée de caisse enregistrée"));
    }

    @PostMapping("/{date}/sortie")
    @PreAuthorize("hasAnyAuthority('ROLE_RH','ROLE_CAISSIER','ROLE_FINANCE','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sortie(@PathVariable String date, @RequestBody Map<String, Object> req) {
        Map<String, Object> caisse = Map.of("date", date, "soldeCourant", 0, "lignes", List.of());
        return ResponseEntity.ok(ApiResponse.succes(caisse, "Sortie de caisse enregistrée"));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_RH','ROLE_CAISSIER','ROLE_FINANCE','ROLE_VALIDATEUR','ROLE_DIRECTION','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> parPeriode(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 caisse(s)"));
    }
}
