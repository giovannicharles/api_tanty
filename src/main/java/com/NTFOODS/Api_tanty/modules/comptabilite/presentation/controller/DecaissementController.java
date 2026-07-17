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
import java.util.concurrent.atomic.AtomicLong;
import static java.util.Map.entry;

@RestController
@RequestMapping("/api/comptabilite/decaissements")
@Tag(name = "Comptabilité — Décaissements", description = "Workflow décaissement 3 étapes")
public class DecaissementController {

    private final AtomicLong counter = new AtomicLong(0);

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_VALIDATEUR','ROLE_DIRECTION','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> lister() {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 décaissement(s)"));
    }

    @GetMapping("/{numero}")
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_VALIDATEUR','ROLE_DIRECTION','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> get(@PathVariable String numero) {
        Map<String, Object> d = Map.of("numero", numero, "statut", "PROPOSE");
        return ResponseEntity.ok(ApiResponse.succes(d, "Décaissement"));
    }

    @GetMapping("/periode")
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_VALIDATEUR','ROLE_DIRECTION','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> parPeriode(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 décaissement(s)"));
    }

    @GetMapping("/statut/{statut}")
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_VALIDATEUR','ROLE_DIRECTION','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> parStatut(@PathVariable String statut) {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 décaissement(s)"));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> proposer(@RequestBody Map<String, Object> req) {
        long id = counter.incrementAndGet();
        String numero = "DEC-" + LocalDate.now().getYear() + "-" + String.format("%05d", id);
        double montant = req.get("montantFCFA") instanceof Number n ? n.doubleValue() : 0;
        Map<String, Object> d = Map.ofEntries(
            entry("id", id),
            entry("numero", numero),
            entry("typeDepense", req.getOrDefault("typeDepense", "")),
            entry("matriculeProposant", req.getOrDefault("matriculeProposant", "")),
            entry("montantFCFA", montant),
            entry("beneficiaire", req.getOrDefault("beneficiaire", "")),
            entry("dateDepense", req.getOrDefault("dateDepense", LocalDate.now().toString())),
            entry("motif", req.getOrDefault("motif", "")),
            entry("statut", "PROPOSE")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.succes(d, "Décaissement proposé : " + numero));
    }

    @PatchMapping("/{numero}/valider-dg")
    @PreAuthorize("hasAnyAuthority('ROLE_DIRECTION','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validerDG(@PathVariable String numero, @RequestParam String matriculeDG) {
        Map<String, Object> d = Map.of("numero", numero, "statut", "VALIDE_DG", "matriculeDG", matriculeDG);
        return ResponseEntity.ok(ApiResponse.succes(d, "Décaissement validé par DG"));
    }

    @PatchMapping("/{numero}/approuver-dg")
    @PreAuthorize("hasAnyAuthority('ROLE_DIRECTION','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> approuverDG(@PathVariable String numero, @RequestParam String matriculeDG) {
        Map<String, Object> d = Map.of("numero", numero, "statut", "APPROUVE_DG", "matriculeDG", matriculeDG);
        return ResponseEntity.ok(ApiResponse.succes(d, "Décaissement approuvé par DG"));
    }

    @PatchMapping("/{numero}/executer")
    @PreAuthorize("hasAnyAuthority('ROLE_RH','ROLE_CAISSIER','ROLE_FINANCE','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> executer(@PathVariable String numero, @RequestParam String matriculeExecuteur) {
        Map<String, Object> d = Map.of("numero", numero, "statut", "EXECUTE", "matriculeExecuteur", matriculeExecuteur);
        return ResponseEntity.ok(ApiResponse.succes(d, "Décaissement exécuté"));
    }

    @PatchMapping("/{numero}/annuler")
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_DIRECTION','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> annuler(@PathVariable String numero, @RequestParam String motif, @RequestParam String matricule) {
        Map<String, Object> d = Map.of("numero", numero, "statut", "ANNULE", "motifAnnulation", motif);
        return ResponseEntity.ok(ApiResponse.succes(d, "Décaissement annulé"));
    }
}
