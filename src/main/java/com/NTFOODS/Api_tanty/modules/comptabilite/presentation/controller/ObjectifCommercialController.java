package com.NTFOODS.Api_tanty.modules.comptabilite.presentation.controller;

import com.NTFOODS.Api_tanty.shared.infrastructure.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import static java.util.Map.entry;

@RestController
@RequestMapping("/api/comptabilite/objectifs")
@Tag(name = "Comptabilité — Objectifs commerciaux", description = "Objectifs hebdomadaires global + par gamme")
public class ObjectifCommercialController {

    private final AtomicLong counter = new AtomicLong(0);

    @GetMapping("/commercial/{matricule}")
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_VALIDATEUR','ROLE_DIRECTION','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> parCommercial(@PathVariable String matricule) {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 objectif(s)"));
    }

    @GetMapping("/semaine")
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_VALIDATEUR','ROLE_DIRECTION','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> parSemaine(@RequestParam String semaineDebut) {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 objectif(s)"));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_FINANCE','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> definir(@RequestBody Map<String, Object> req) {
        long id = counter.incrementAndGet();
        Map<String, Object> obj = Map.ofEntries(
            entry("id", id),
            entry("matriculeCommercial", req.getOrDefault("matriculeCommercial", "")),
            entry("semaineDebut", req.getOrDefault("semaineDebut", "")),
            entry("semaineFin", req.getOrDefault("semaineFin", "")),
            entry("objectifGlobal", req.getOrDefault("objectifGlobal", 0)),
            entry("objectifFarines", req.getOrDefault("objectifFarines", 0)),
            entry("objectifEaux", req.getOrDefault("objectifEaux", 0)),
            entry("objectifJus", req.getOrDefault("objectifJus", 0)),
            entry("objectifSnacks", req.getOrDefault("objectifSnacks", 0)),
            entry("objectifBiscuits", req.getOrDefault("objectifBiscuits", 0)),
            entry("objectifConfiseries", req.getOrDefault("objectifConfiseries", 0)),
            entry("matriculeDefinisseur", req.getOrDefault("matriculeDefinisseur", "")),
            entry("dateDefinition", java.time.LocalDate.now().toString())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.succes(obj, "Objectif défini"));
    }
}
