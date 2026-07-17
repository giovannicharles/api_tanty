package com.NTFOODS.Api_tanty.modules.commercial.presentation.controller;

import com.NTFOODS.Api_tanty.shared.infrastructure.dto.ApiResponse;
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
@RequestMapping("/api/commercial/recouvrements")
@Tag(name = "Commercial — Recouvrements", description = "Suivi des créances clients")
public class RecouvrementController {

    private final AtomicLong counter = new AtomicLong(0);

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_COMMERCIAL','ROLE_RH','ROLE_CAISSIER','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> creer(@RequestBody Map<String, Object> req) {
        long id = counter.incrementAndGet();
        String reference = "REC-" + req.getOrDefault("codeClient", "X") + "-" + System.currentTimeMillis();
        double montantDu = req.get("montantDu") instanceof Number n ? n.doubleValue() : 0;
        Map<String, Object> recouvrement = Map.ofEntries(
            entry("id", id),
            entry("referenceRecouvrement", reference),
            entry("codeClient", req.getOrDefault("codeClient", "")),
            entry("nomClient", req.getOrDefault("nomClient", "")),
            entry("montantDu", montantDu),
            entry("montantRembourse", 0),
            entry("montantRestant", montantDu),
            entry("dateEcheance", req.getOrDefault("dateEcheance", LocalDate.now().toString())),
            entry("statut", "EN_ATTENTE"),
            entry("matriculeCommercial", req.getOrDefault("matriculeCommercial", ""))
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.succes(recouvrement, "Recouvrement créé"));
    }

    @PatchMapping("/{reference}/rembourser")
    @PreAuthorize("hasAnyAuthority('ROLE_RH','ROLE_CAISSIER','ROLE_FINANCE','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> rembourser(
        @PathVariable String reference, @RequestParam double montant) {
        Map<String, Object> recouvrement = Map.of(
            "referenceRecouvrement", reference,
            "montantRembourse", montant,
            "montantRestant", 0,
            "statut", "PARTIEL"
        );
        return ResponseEntity.ok(ApiResponse.succes(recouvrement, "Remboursement enregistré"));
    }

    @PatchMapping("/{reference}/contentieux")
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_DIRECTION','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> passerEnContentieux(
        @PathVariable String reference, @RequestParam String motif) {
        Map<String, Object> recouvrement = Map.of(
            "referenceRecouvrement", reference,
            "statut", "CONTENTIEUX",
            "observations", motif
        );
        return ResponseEntity.ok(ApiResponse.succes(recouvrement, "Passé en contentieux"));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> lister(
        @RequestParam(required = false) String commercial,
        @RequestParam(required = false) String client,
        @RequestParam(required = false) String statut) {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 recouvrement(s)"));
    }
}
