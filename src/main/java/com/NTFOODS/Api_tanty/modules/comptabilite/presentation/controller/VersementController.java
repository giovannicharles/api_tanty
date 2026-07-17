package com.NTFOODS.Api_tanty.modules.comptabilite.presentation.controller;

import com.NTFOODS.Api_tanty.shared.infrastructure.dto.ApiResponse;
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
@RequestMapping("/api/comptabilite/versements")
@Tag(name = "Comptabilité — Versements", description = "Versements des commerciaux et alertes rouges")
public class VersementController {

    private final AtomicLong counter = new AtomicLong(0);

    @GetMapping("/reconciliation")
    @PreAuthorize("hasAnyAuthority('ROLE_RH','ROLE_CAISSIER','ROLE_FINANCE','ROLE_VALIDATEUR','ROLE_DIRECTION','ROLE_ADMIN')")
    @Operation(summary = "Prévisualiser la réconciliation financière immédiate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> reconciliation(
        @RequestParam String matricule,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Map<String, Object> result = Map.of(
            "matricule", matricule,
            "date", date.toString(),
            "caDeclare", 0,
            "valeurRetoursValides", 0,
            "resultatNet", 0
        );
        return ResponseEntity.ok(ApiResponse.succes(result, "Réconciliation calculée"));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_COMMERCIAL','ROLE_RH','ROLE_CAISSIER','ROLE_FINANCE','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> enregistrer(@RequestBody Map<String, Object> req) {
        long id = counter.incrementAndGet();
        String reference = "VER-" + LocalDate.now().getYear() + "-" + String.format("%05d", id);
        double montantAttendu = req.get("montantAttendu") instanceof Number n ? n.doubleValue() : 0;
        double cashVerse = req.get("cashVerse") instanceof Number n2 ? n2.doubleValue() : 0;
        Map<String, Object> versement = Map.ofEntries(
            entry("id", id),
            entry("referenceVersement", reference),
            entry("matriculeCommercial", req.getOrDefault("matriculeCommercial", "")),
            entry("montantAttendu", montantAttendu),
            entry("cashVerse", cashVerse),
            entry("ecart", cashVerse - montantAttendu),
            entry("alerteRouge", false),
            entry("typeVersement", req.getOrDefault("typeVersement", "ESPECES")),
            entry("date", req.getOrDefault("date", LocalDate.now().toString())),
            entry("statut", "EN_ATTENTE")
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.succes(versement, "Versement enregistré"));
    }

    @PatchMapping("/{reference}/valider")
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_ADMIN')")
    @Operation(summary = "Valider un versement")
    public ResponseEntity<ApiResponse<Map<String, Object>>> valider(
        @PathVariable String reference,
        @RequestParam String matriculeValidateur,
        @RequestParam String codeOtp) {
        Map<String, Object> versement = Map.of(
            "referenceVersement", reference, "statut", "VALIDE",
            "matriculeValidateur", matriculeValidateur
        );
        return ResponseEntity.ok(ApiResponse.succes(versement, "Versement validé"));
    }

    @PatchMapping("/{reference}/justifier")
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_VALIDATEUR','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> justifier(
        @PathVariable String reference,
        @RequestBody Map<String, Object> req) {
        Map<String, Object> versement = Map.of(
            "referenceVersement", reference,
            "statut", "VALIDE",
            "justificationEcart", req.getOrDefault("justification", "")
        );
        return ResponseEntity.ok(ApiResponse.succes(versement, "Écart justifié"));
    }

    @GetMapping("/alertes")
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_VALIDATEUR','ROLE_DIRECTION','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> alertesRouges() {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 alerte(s) rouge(s)"));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_COMMERCIAL','ROLE_RH','ROLE_CAISSIER','ROLE_FINANCE','ROLE_VALIDATEUR','ROLE_DIRECTION','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> lister(
        @RequestParam(required = false) String commercial,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam(required = false) String statut) {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 versement(s)"));
    }
}
