package com.NTFOODS.Api_tanty.modules.stock.presentation;

import com.NTFOODS.Api_tanty.modules.stock.application.dto.MaterielDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * MaterielController - Controller REST pour la gestion des matériels
 * Fournit les endpoints pour la gestion des matériels de l'entreprise
 */
@RestController
@RequestMapping("/api/v1/stock/materiels")
@Tag(name = "Matériels", description = "API pour la gestion des matériels")
public class MaterielController {

    /**
     * Endpoint pour récupérer tous les matériels
     * @return Liste des matériels
     */
    @GetMapping
    @Operation(
        summary = "Récupérer tous les matériels",
        description = "Retourne la liste de tous les matériels de l'entreprise"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liste des matériels récupérée avec succès",
            content = @Content(schema = @Schema(implementation = MaterielDTO.class))
        )
    })
    public ResponseEntity<List<MaterielDTO>> getAllMateriels() {
        // TODO: Implémenter la logique pour récupérer les matériels depuis la base de données
        List<MaterielDTO> materiels = new ArrayList<>();
        
        // Données mockées pour l'instant
        materiels.add(new MaterielDTO(
            "1", "MAT-001", "Perceuse à colonne", "OUTILLAGE", 1,
            "unité", "Atelier A", "DISPONIBLE", "2024-01-15",
            "Bosch", 150000.0, "2024-05-10", "2024-11-10"
        ));
        
        materiels.add(new MaterielDTO(
            "2", "MAT-002", "Four industriel", "EQUIPEMENT", 1,
            "unité", "Zone Production", "EN_USAGE", "2023-06-20",
            "Thermix", 2500000.0, "2024-04-15", "2024-10-15"
        ));
        
        return ResponseEntity.ok(materiels);
    }

    /**
     * Endpoint pour récupérer un matériel par son ID
     * @param id Identifiant du matériel
     * @return Matériel correspondant
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Récupérer un matériel par ID",
        description = "Retourne un matériel spécifique par son identifiant"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Matériel récupéré avec succès",
            content = @Content(schema = @Schema(implementation = MaterielDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Matériel non trouvé",
            content = @Content
        )
    })
    public ResponseEntity<MaterielDTO> getMaterielById(@PathVariable String id) {
        // TODO: Implémenter la logique pour récupérer un matériel par ID
        return ResponseEntity.ok(new MaterielDTO());
    }

    /**
     * Endpoint pour créer un nouveau matériel
     * @param materielDTO Données du matériel à créer
     * @return Matériel créé
     */
    @PostMapping
    @Operation(
        summary = "Créer un nouveau matériel",
        description = "Crée un nouveau matériel dans le système"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Matériel créé avec succès",
            content = @Content(schema = @Schema(implementation = MaterielDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Données invalides",
            content = @Content
        )
    })
    public ResponseEntity<MaterielDTO> createMateriel(@RequestBody MaterielDTO materielDTO) {
        // TODO: Implémenter la logique pour créer un matériel
        return ResponseEntity.ok(materielDTO);
    }

    /**
     * Endpoint pour mettre à jour un matériel
     * @param id Identifiant du matériel à mettre à jour
     * @param materielDTO Données du matériel à mettre à jour
     * @return Matériel mis à jour
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Mettre à jour un matériel",
        description = "Met à jour un matériel existant"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Matériel mis à jour avec succès",
            content = @Content(schema = @Schema(implementation = MaterielDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Matériel non trouvé",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Données invalides",
            content = @Content
        )
    })
    public ResponseEntity<MaterielDTO> updateMateriel(@PathVariable String id, @RequestBody MaterielDTO materielDTO) {
        // TODO: Implémenter la logique pour mettre à jour un matériel
        return ResponseEntity.ok(materielDTO);
    }

    /**
     * Endpoint pour supprimer un matériel
     * @param id Identifiant du matériel à supprimer
     * @return Réponse vide
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Supprimer un matériel",
        description = "Supprime un matériel du système"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Matériel supprimé avec succès",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Matériel non trouvé",
            content = @Content
        )
    })
    public ResponseEntity<Void> deleteMateriel(@PathVariable String id) {
        // TODO: Implémenter la logique pour supprimer un matériel
        return ResponseEntity.noContent().build();
    }
}
