package com.NTFOODS.Api_tanty.modules.production.presentation.controller;

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
@RequestMapping("/api/production")
@Tag(name = "Production", description = "Lots, ordres de fabrication, PPH, sessions, fiches, registres")
@PreAuthorize("hasAnyAuthority('ROLE_PRODUCTION','ROLE_DIRECTION','ROLE_ADMIN')")
public class ProductionController {

    private final AtomicLong counter = new AtomicLong(0);

    // ── Lots ───────────────────────────────────────────────────

    @GetMapping("/lots")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getLots(
        @RequestParam(required = false) String statut) {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 lot(s)"));
    }

    @PostMapping("/lots")
    public ResponseEntity<ApiResponse<Map<String, Object>>> declarerLot(@RequestBody Map<String, Object> req) {
        long id = counter.incrementAndGet();
        String numero = "LOT-" + LocalDate.now().getYear() + "-" + String.format("%05d", id);
        Map<String, Object> lot = Map.ofEntries(
            entry("id", id),
            entry("numeroLot", numero),
            entry("codeProduit", req.getOrDefault("codeProduit", "")),
            entry("designationProduit", ""),
            entry("quantiteKg", req.getOrDefault("quantiteKg", 0)),
            entry("quantiteUnites", req.getOrDefault("quantiteUnites", 0)),
            entry("dlc", req.getOrDefault("dlc", "")),
            entry("dateDeclaration", LocalDate.now().toString()),
            entry("matriculeDeclarant", ""),
            entry("statut", "DECLARED_BY_PRODUCTION")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.succes(lot, "Lot déclaré : " + numero));
    }

    @GetMapping("/lots/a-valider")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> lotsAValider() {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 lot(s) à valider"));
    }

    @PatchMapping("/lots/{numeroLot}/valider")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validerLot(@PathVariable String numeroLot) {
        Map<String, Object> lot = Map.of("numeroLot", numeroLot, "statut", "VALIDATED_BY_STOCK");
        return ResponseEntity.ok(ApiResponse.succes(lot, "Lot validé"));
    }

    @PatchMapping("/lots/{numeroLot}/rejeter")
    public ResponseEntity<ApiResponse<Map<String, Object>>> rejeterLot(@PathVariable String numeroLot, @RequestParam String motif) {
        Map<String, Object> lot = Map.of("numeroLot", numeroLot, "statut", "REJETE");
        return ResponseEntity.ok(ApiResponse.succes(lot, "Lot rejeté"));
    }

    // ── Ordres de Fabrication ─────────────────────────────────

    @GetMapping("/ordres-fabrication")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getOFs(
        @RequestParam(required = false) String statut) {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 OF(s)"));
    }

    @GetMapping("/ordres-fabrication/{idOF}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOF(@PathVariable String idOF) {
        Map<String, Object> of = Map.of("idOF", idOF, "statut", "EN_ATTENTE");
        return ResponseEntity.ok(ApiResponse.succes(of, "Ordre de fabrication"));
    }

    // ── PPH (Plan de Production Hebdomadaire) ─────────────────

    @GetMapping("/pph")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPPHs(
        @RequestParam(required = false) String statut) {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 PPH(s)"));
    }

    @GetMapping("/pph/en-cours")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPPHEnCours() {
        return ResponseEntity.ok(ApiResponse.succes(null, "Aucun PPH en cours"));
    }

    @GetMapping("/pph/{reference}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPPH(@PathVariable String reference) {
        Map<String, Object> pph = Map.of("referencePPH", reference, "statut", "CREE");
        return ResponseEntity.ok(ApiResponse.succes(pph, "PPH"));
    }

    @PostMapping("/pph")
    public ResponseEntity<ApiResponse<Map<String, Object>>> creerPPH(@RequestBody Map<String, Object> req) {
        long id = counter.incrementAndGet();
        String reference = "PPH-" + LocalDate.now().getYear() + "-" + String.format("%05d", id);
        Map<String, Object> pph = Map.ofEntries(
            entry("id", id),
            entry("referencePPH", reference),
            entry("referenceBC", req.getOrDefault("referenceBC", "")),
            entry("dateDebut", req.getOrDefault("dateDebut", "")),
            entry("dateFin", req.getOrDefault("dateFin", "")),
            entry("statut", "CREE"),
            entry("matieresPremieres", List.of()),
            entry("produitsFinis", List.of())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.succes(pph, "PPH créé : " + reference));
    }

    @PatchMapping("/pph/{reference}/valider")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validerPPH(@PathVariable String reference) {
        Map<String, Object> pph = Map.of("referencePPH", reference, "statut", "VALIDE");
        return ResponseEntity.ok(ApiResponse.succes(pph, "PPH validé"));
    }

    @PatchMapping("/pph/{reference}/demarrer")
    public ResponseEntity<ApiResponse<Map<String, Object>>> demarrerPPH(@PathVariable String reference) {
        Map<String, Object> pph = Map.of("referencePPH", reference, "statut", "EN_COURS");
        return ResponseEntity.ok(ApiResponse.succes(pph, "PPH démarré"));
    }

    @PatchMapping("/pph/{reference}/cloturer")
    public ResponseEntity<ApiResponse<Map<String, Object>>> cloturerPPH(@PathVariable String reference) {
        Map<String, Object> pph = Map.of("referencePPH", reference, "statut", "CLOTURE");
        return ResponseEntity.ok(ApiResponse.succes(pph, "PPH clôturé"));
    }

    // ── Sessions Broyage ──────────────────────────────────────

    @GetMapping("/broyage/sessions")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getSessionsBroyage(
        @RequestParam(required = false) String referencePPH,
        @RequestParam(required = false) String debut,
        @RequestParam(required = false) String fin) {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 session(s)"));
    }

    @GetMapping("/broyage/sessions/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSessionBroyage(@PathVariable long id) {
        Map<String, Object> s = Map.of("id", id, "statut", "EN_COURS");
        return ResponseEntity.ok(ApiResponse.succes(s, "Session broyage"));
    }

    @PostMapping("/broyage/sessions")
    public ResponseEntity<ApiResponse<Map<String, Object>>> ouvrirSessionBroyage(@RequestBody Map<String, Object> req) {
        long id = counter.incrementAndGet();
        Map<String, Object> s = Map.ofEntries(
            entry("id", id),
            entry("date", req.getOrDefault("date", LocalDate.now().toString())),
            entry("referencePPH", req.getOrDefault("referencePPH", "")),
            entry("statut", "EN_COURS"),
            entry("realisations", List.of()),
            entry("indicateursGlobaux", Map.of("totalPoudreKg", 0))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.succes(s, "Session broyage ouverte"));
    }

    @PostMapping("/broyage/sessions/realisations")
    public ResponseEntity<ApiResponse<Map<String, Object>>> enregistrerRealisation(@RequestBody Map<String, Object> req) {
        Map<String, Object> s = Map.of("statut", "EN_COURS", "realisations", List.of());
        return ResponseEntity.ok(ApiResponse.succes(s, "Réalisation enregistrée"));
    }

    @PostMapping("/broyage/sessions/cloturer")
    public ResponseEntity<ApiResponse<Map<String, Object>>> cloturerSessionBroyage(@RequestBody Map<String, Object> req) {
        Map<String, Object> s = Map.of("statut", "CLOTUREE");
        return ResponseEntity.ok(ApiResponse.succes(s, "Session broyage clôturée"));
    }

    // ── Sessions Dosage ───────────────────────────────────────

    @PostMapping("/dosage")
    public ResponseEntity<ApiResponse<Map<String, Object>>> ouvrirSessionDosage(@RequestBody Map<String, Object> req) {
        long id = counter.incrementAndGet();
        Map<String, Object> s = Map.ofEntries(
            entry("id", id),
            entry("date", req.getOrDefault("date", LocalDate.now().toString())),
            entry("referencePPH", req.getOrDefault("referencePPH", "")),
            entry("matriculeAgentDoseur", req.getOrDefault("matriculeAgentDoseur", "")),
            entry("statut", "EN_COURS"),
            entry("futsProduits", 0),
            entry("futsCasses", 0),
            entry("machines", List.of()),
            entry("matieresUtilisees", List.of())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.succes(s, "Session dosage ouverte"));
    }

    @PostMapping("/dosage/{id}/matiere")
    public ResponseEntity<ApiResponse<Map<String, Object>>> enregistrerMatiereDosage(
        @PathVariable long id, @RequestParam String typeMatiere, @RequestParam double quantiteKg) {
        Map<String, Object> s = Map.of("id", id, "statut", "EN_COURS");
        return ResponseEntity.ok(ApiResponse.succes(s, "Matière enregistrée"));
    }

    @PostMapping("/dosage/{id}/machine")
    public ResponseEntity<ApiResponse<Map<String, Object>>> enregistrerMachineDosage(
        @PathVariable long id, @RequestParam String machineId) {
        Map<String, Object> s = Map.of("id", id, "statut", "EN_COURS");
        return ResponseEntity.ok(ApiResponse.succes(s, "Machine enregistrée"));
    }

    @PostMapping("/dosage/{id}/futs")
    public ResponseEntity<ApiResponse<Map<String, Object>>> enregistrerFutsDosage(
        @PathVariable long id, @RequestParam int nbProduits, @RequestParam int nbCasses) {
        Map<String, Object> s = Map.of("id", id, "futsProduits", nbProduits, "futsCasses", nbCasses, "statut", "EN_COURS");
        return ResponseEntity.ok(ApiResponse.succes(s, "Fûts enregistrés"));
    }

    @PostMapping("/dosage/{id}/justifier")
    public ResponseEntity<ApiResponse<Map<String, Object>>> justifierEcartDosage(
        @PathVariable long id, @RequestParam String justification) {
        Map<String, Object> s = Map.of("id", id, "statut", "EN_COURS");
        return ResponseEntity.ok(ApiResponse.succes(s, "Écart justifié"));
    }

    @PatchMapping("/dosage/{id}/valider")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validerSessionDosage(@PathVariable long id) {
        Map<String, Object> s = Map.of("id", id, "statut", "VALIDEE");
        return ResponseEntity.ok(ApiResponse.succes(s, "Session dosage validée"));
    }

    @GetMapping("/dosage/pph/{reference}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getSessionsDosageParPPH(@PathVariable String reference) {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 session(s)"));
    }

    @GetMapping("/dosage/date/{date}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getSessionsDosageParDate(@PathVariable String date) {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 session(s)"));
    }

    // ── Fiches de Production ──────────────────────────────────

    @GetMapping("/fiches/pph/{referencePPH}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getFichesParPPH(@PathVariable String referencePPH) {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 fiche(s)"));
    }

    @GetMapping("/fiches/pph/{referencePPH}/date/{date}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFiche(
        @PathVariable String referencePPH, @PathVariable String date) {
        Map<String, Object> f = Map.of("referencePPH", referencePPH, "date", date);
        return ResponseEntity.ok(ApiResponse.succes(f, "Fiche de production"));
    }

    @PostMapping("/fiches")
    public ResponseEntity<ApiResponse<Map<String, Object>>> creerFiche(@RequestBody Map<String, Object> req) {
        long id = counter.incrementAndGet();
        Map<String, Object> f = Map.ofEntries(
            entry("id", id),
            entry("referencePPH", req.getOrDefault("referencePPH", "")),
            entry("date", req.getOrDefault("date", LocalDate.now().toString())),
            entry("codeProduit", req.getOrDefault("codeProduit", "")),
            entry("designation", req.getOrDefault("designation", "")),
            entry("quantiteProduite", req.getOrDefault("quantiteProduite", 0)),
            entry("unite", req.getOrDefault("unite", "kg"))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.succes(f, "Fiche créée"));
    }

    @PatchMapping("/fiches/pph/{referencePPH}/date/{date}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> modifierFiche(
        @PathVariable String referencePPH, @PathVariable String date, @RequestBody Map<String, Object> req) {
        Map<String, Object> f = Map.of("referencePPH", referencePPH, "date", date);
        return ResponseEntity.ok(ApiResponse.succes(f, "Fiche modifiée"));
    }

    // ── Affectations ──────────────────────────────────────────

    @GetMapping("/affectations/pph/{referencePPH}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAffectations(@PathVariable String referencePPH) {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 affectation(s)"));
    }

    @PostMapping("/affectations")
    public ResponseEntity<ApiResponse<Map<String, Object>>> creerAffectation(@RequestBody Map<String, Object> req) {
        long id = counter.incrementAndGet();
        Map<String, Object> a = Map.ofEntries(
            entry("id", id),
            entry("referencePPH", req.getOrDefault("referencePPH", "")),
            entry("matriculeEmploye", req.getOrDefault("matriculeEmploye", "")),
            entry("poste", req.getOrDefault("poste", "")),
            entry("date", req.getOrDefault("date", LocalDate.now().toString()))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.succes(a, "Affectation créée"));
    }

    @PatchMapping("/affectations/{id}/terminer")
    public ResponseEntity<ApiResponse<Map<String, Object>>> terminerAffectation(@PathVariable long id) {
        Map<String, Object> a = Map.of("id", id);
        return ResponseEntity.ok(ApiResponse.succes(a, "Affectation terminée"));
    }

    // ── Registres ─────────────────────────────────────────────

    @GetMapping("/registres")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRegistres(
        @RequestParam(required = false) String date) {
        return ResponseEntity.ok(ApiResponse.succes(List.of(), "0 registre(s)"));
    }

    @GetMapping("/registres/{date}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRegistre(@PathVariable String date) {
        Map<String, Object> r = Map.of("date", date, "lignes", List.of());
        return ResponseEntity.ok(ApiResponse.succes(r, "Registre"));
    }

    @PostMapping("/registres/{date}/lignes")
    public ResponseEntity<ApiResponse<Map<String, Object>>> ajouterLigneRegistre(
        @PathVariable String date, @RequestBody Map<String, Object> req) {
        long id = counter.incrementAndGet();
        Map<String, Object> ligne = Map.ofEntries(
            entry("id", id),
            entry("matriculeEmploye", req.getOrDefault("matriculeEmploye", "")),
            entry("nomEmploye", req.getOrDefault("nomEmploye", "")),
            entry("poste", req.getOrDefault("poste", "")),
            entry("present", req.getOrDefault("present", false)),
            entry("heureArrivee", req.getOrDefault("heureArrivee", "")),
            entry("qteRealisee", req.getOrDefault("qteRealisee", 0)),
            entry("unite", req.getOrDefault("unite", "kg"))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.succes(ligne, "Ligne ajoutée au registre"));
    }

    @PatchMapping("/registres/{date}/lignes/{matricule}/presence")
    public ResponseEntity<ApiResponse<Map<String, Object>>> modifierPresence(
        @PathVariable String date, @PathVariable String matricule, @RequestParam boolean present) {
        Map<String, Object> ligne = Map.of("matriculeEmploye", matricule, "present", present);
        return ResponseEntity.ok(ApiResponse.succes(ligne, "Présence modifiée"));
    }
}
