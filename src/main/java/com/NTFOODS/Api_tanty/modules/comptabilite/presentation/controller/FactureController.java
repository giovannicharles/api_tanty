package com.NTFOODS.Api_tanty.modules.comptabilite.presentation.controller;

import com.NTFOODS.Api_tanty.shared.infrastructure.dto.ApiResponse;
import com.NTFOODS.Api_tanty.shared.infrastructure.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import static java.util.Map.entry;

@RestController
@RequestMapping("/api/comptabilite/factures")
@Tag(name = "Comptabilité — Factures", description = "Émission de factures et avoirs")
public class FactureController {

    private final AtomicLong counter = new AtomicLong(0);

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_RH','ROLE_CAISSIER','ROLE_FINANCE','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> emettre(@RequestBody Map<String, Object> req) {
        long id = counter.incrementAndGet();
        String numero = "FAC-" + LocalDate.now().getYear() + "-" + String.format("%05d", id);
        Map<String, Object> facture = Map.ofEntries(
            entry("id", id),
            entry("numeroFacture", numero),
            entry("codeClient", req.getOrDefault("codeClient", "")),
            entry("typeFacture", req.getOrDefault("typeFacture", "FACTURE")),
            entry("dateEmission", LocalDate.now().toString()),
            entry("statut", "EMISE"),
            entry("matriculeEmetteur", req.getOrDefault("matriculeEmetteur", "")),
            entry("montantTotalHT", 0),
            entry("montantTotalTVA", 0),
            entry("montantTotalTTC", 0),
            entry("lignes", req.getOrDefault("lignes", List.of()))
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.succes(facture, "Facture émise : " + numero));
    }

    @PatchMapping("/{numero}/payer")
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> marquerPayee(@PathVariable String numero) {
        Map<String, Object> facture = Map.of(
            "numeroFacture", numero, "statut", "PAYEE"
        );
        return ResponseEntity.ok(ApiResponse.succes(facture, "Facture payée"));
    }

    @PatchMapping("/{numero}/annuler")
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_DIRECTION','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> annuler(@PathVariable String numero) {
        Map<String, Object> facture = Map.of(
            "numeroFacture", numero, "statut", "ANNULEE"
        );
        return ResponseEntity.ok(ApiResponse.succes(facture, "Facture annulée"));
    }

    @GetMapping("/{numero}")
    @PreAuthorize("hasAnyAuthority('ROLE_RH','ROLE_CAISSIER','ROLE_FINANCE','ROLE_VALIDATEUR','ROLE_DIRECTION','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> consulter(@PathVariable String numero) {
        Map<String, Object> facture = Map.ofEntries(
            entry("id", 0), entry("numeroFacture", numero), entry("codeClient", ""),
            entry("typeFacture", "FACTURE"), entry("dateEmission", LocalDate.now().toString()),
            entry("statut", "EMISE"), entry("matriculeEmetteur", ""),
            entry("montantTotalHT", 0), entry("montantTotalTVA", 0), entry("montantTotalTTC", 0),
            entry("lignes", List.of())
        );
        return ResponseEntity.ok(ApiResponse.succes(facture, "Facture"));
    }

    @GetMapping("/statut/{statut}")
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_VALIDATEUR','ROLE_DIRECTION','ROLE_ADMIN')")
    @Operation(summary = "Factures par statut (paginé)")
    public ResponseEntity<ApiResponse<PageResponse<Map<String, Object>>>> parStatut(@PathVariable String statut) {
        PageResponse<Map<String, Object>> page = new PageResponse<>(List.of(), 0, 20, 0);
        return ResponseEntity.ok(ApiResponse.succes(page, "0 facture(s)"));
    }

    @GetMapping("/client/{codeClient}")
    @PreAuthorize("hasAnyAuthority('ROLE_RH','ROLE_CAISSIER','ROLE_FINANCE','ROLE_VALIDATEUR','ROLE_DIRECTION','ROLE_ADMIN')")
    @Operation(summary = "Factures d'un client (paginé)")
    public ResponseEntity<ApiResponse<PageResponse<Map<String, Object>>>> parClient(@PathVariable String codeClient) {
        PageResponse<Map<String, Object>> page = new PageResponse<>(List.of(), 0, 20, 0);
        return ResponseEntity.ok(ApiResponse.succes(page, "0 facture(s)"));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_VALIDATEUR','ROLE_DIRECTION','ROLE_ADMIN')")
    @Operation(summary = "Factures par période (paginé)")
    public ResponseEntity<ApiResponse<PageResponse<Map<String, Object>>>> parPeriode(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        PageResponse<Map<String, Object>> page = new PageResponse<>(List.of(), 0, 20, 0);
        return ResponseEntity.ok(ApiResponse.succes(page, "0 facture(s)"));
    }
}
