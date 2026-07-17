package com.NTFOODS.Api_tanty.modules.commercial.presentation.controller;

import com.NTFOODS.Api_tanty.shared.infrastructure.dto.ApiResponse;
import com.NTFOODS.Api_tanty.shared.infrastructure.dto.PageResponse;
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
@RequestMapping("/api/commercial/precommandes")
@Tag(name = "Commercial — Pré-commandes", description = "Pré-commandes et workflow de validation")
public class PreCommandeController {

    private final AtomicLong counter = new AtomicLong(0);

    @GetMapping("/a-valider")
    @PreAuthorize("hasAnyAuthority('ROLE_RH','ROLE_CAISSIER','ROLE_FINANCE','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<Map<String, Object>>>> aValider(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        PageResponse<Map<String, Object>> p = new PageResponse<>(List.of(), page, size, 0);
        return ResponseEntity.ok(ApiResponse.succes(p, "0 pré-commande(s) à valider"));
    }

    @GetMapping("/date-effet/{date}")
    @PreAuthorize("hasAnyAuthority('ROLE_RH','ROLE_CAISSIER','ROLE_FINANCE','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<Map<String, Object>>>> parDateEffet(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        PageResponse<Map<String, Object>> p = new PageResponse<>(List.of(), page, size, 0);
        return ResponseEntity.ok(ApiResponse.succes(p, "0 pré-commande(s)"));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_COMMERCIAL','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> creer(@RequestBody Map<String, Object> req) {
        long id = counter.incrementAndGet();
        String numero = "PC-" + LocalDate.now().getYear() + "-" + String.format("%05d", id);
        Map<String, Object> pc = Map.ofEntries(
            entry("id", id),
            entry("numeroPreCommande", numero),
            entry("matriculeCommercial", req.getOrDefault("matriculeCommercial", "")),
            entry("dateSoumission", req.getOrDefault("dateSoumission", LocalDate.now().toString())),
            entry("statut", "SOUMISE"),
            entry("lignes", req.getOrDefault("lignes", List.of()))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.succes(pc, "Pré-commande créée : " + numero));
    }

    @PatchMapping("/{numero}/valider-secretaire")
    @PreAuthorize("hasAnyAuthority('ROLE_RH','ROLE_CAISSIER','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validerSecretaire(
        @PathVariable String numero, @RequestParam String matriculeSecretaire) {
        Map<String, Object> pc = Map.of("numeroPreCommande", numero, "statut", "VALIDEE_SECRETAIRE");
        return ResponseEntity.ok(ApiResponse.succes(pc, "Pré-commande validée par secrétaire"));
    }

    @PatchMapping("/{numero}/valider-comptable")
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validerComptable(
        @PathVariable String numero, @RequestParam String matriculeComptable) {
        Map<String, Object> pc = Map.of("numeroPreCommande", numero, "statut", "VALIDEE_COMPTABLE");
        return ResponseEntity.ok(ApiResponse.succes(pc, "Pré-commande validée par comptable"));
    }

    @PatchMapping("/{numero}/refuser")
    @PreAuthorize("hasAnyAuthority('ROLE_RH','ROLE_CAISSIER','ROLE_FINANCE','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> refuser(
        @PathVariable String numero, @RequestParam String motif, @RequestParam String matriculeValidateur) {
        Map<String, Object> pc = Map.of("numeroPreCommande", numero, "statut", "REFUSEE", "motifRefus", motif);
        return ResponseEntity.ok(ApiResponse.succes(pc, "Pré-commande refusée"));
    }

    @PatchMapping("/{numero}/livrer")
    @PreAuthorize("hasAnyAuthority('ROLE_RH','ROLE_CAISSIER','ROLE_FINANCE','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> livrer(@PathVariable String numero) {
        Map<String, Object> pc = Map.of("numeroPreCommande", numero, "statut", "LIVREE");
        return ResponseEntity.ok(ApiResponse.succes(pc, "Pré-commande livrée"));
    }
}
