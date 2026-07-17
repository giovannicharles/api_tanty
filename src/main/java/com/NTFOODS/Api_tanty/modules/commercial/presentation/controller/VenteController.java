package com.NTFOODS.Api_tanty.modules.commercial.presentation.controller;

import com.NTFOODS.Api_tanty.shared.infrastructure.dto.ApiResponse;
import com.NTFOODS.Api_tanty.shared.infrastructure.dto.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/commercial/ventes")
@Tag(name = "Commercial — Ventes", description = "Ventes, avoirs et ventes non marchandes")
public class VenteController {

    private final AtomicLong counter = new AtomicLong(0);

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<Map<String, Object>>>> lister(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        PageResponse<Map<String, Object>> p = new PageResponse<>(List.of(), page, size, 0);
        return ResponseEntity.ok(ApiResponse.succes(p, "0 vente(s)"));
    }

    @GetMapping("/commercial/{matricule}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<Map<String, Object>>>> parCommercial(
        @PathVariable String matricule,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        PageResponse<Map<String, Object>> p = new PageResponse<>(List.of(), page, size, 0);
        return ResponseEntity.ok(ApiResponse.succes(p, "0 vente(s)"));
    }

    @PatchMapping("/{numero}/finaliser")
    @PreAuthorize("hasAnyAuthority('ROLE_COMMERCIAL','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> finaliser(@PathVariable String numero) {
        Map<String, Object> v = Map.of("numeroVente", numero, "statut", "FINALISEE");
        return ResponseEntity.ok(ApiResponse.succes(v, "Vente finalisée"));
    }

    @PatchMapping("/{numero}/annuler")
    @PreAuthorize("hasAnyAuthority('ROLE_COMMERCIAL','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> annuler(@PathVariable String numero, @RequestParam String motif) {
        Map<String, Object> v = Map.of("numeroVente", numero, "statut", "ANNULEE");
        return ResponseEntity.ok(ApiResponse.succes(v, "Vente annulée"));
    }

    @PostMapping("/non-marchand")
    @PreAuthorize("hasAnyAuthority('ROLE_COMMERCIAL','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> venteNonMarchand(@RequestBody Map<String, Object> req) {
        long id = counter.incrementAndGet();
        String numero = "VNM-" + LocalDate.now().getYear() + "-" + String.format("%05d", id);
        Map<String, Object> v = Map.ofEntries(
            entry("id", id),
            entry("numeroVente", numero),
            entry("matriculeCommercial", req.getOrDefault("matriculeCommercial", "")),
            entry("codeProduit", req.getOrDefault("codeProduit", "")),
            entry("quantite", req.getOrDefault("quantite", 0)),
            entry("date", req.getOrDefault("date", LocalDate.now().toString())),
            entry("motif", req.getOrDefault("motif", "")),
            entry("statut", "FINALISEE")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.succes(v, "Vente non marchande enregistrée"));
    }

    @PostMapping("/{numeroVente}/avoir")
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> creerAvoir(
        @PathVariable String numeroVente, @RequestBody Map<String, Object> req) {
        long id = counter.incrementAndGet();
        String numeroAvoir = "AVO-" + LocalDate.now().getYear() + "-" + String.format("%05d", id);
        Map<String, Object> a = Map.ofEntries(
            entry("id", id),
            entry("numeroAvoir", numeroAvoir),
            entry("numeroVenteOrigine", numeroVente),
            entry("matriculeComptable", req.getOrDefault("matriculeComptable", "")),
            entry("motif", req.getOrDefault("motif", "")),
            entry("statut", "EN_ATTENTE")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.succes(a, "Avoir créé : " + numeroAvoir));
    }

    @PatchMapping("/avoirs/{numeroAvoir}/valider")
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validerAvoir(@PathVariable String numeroAvoir) {
        Map<String, Object> a = Map.of("numeroAvoir", numeroAvoir, "statut", "VALIDE");
        return ResponseEntity.ok(ApiResponse.succes(a, "Avoir validé"));
    }
}
