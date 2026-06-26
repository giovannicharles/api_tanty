package com.NTFOODS.Api_tanty.modules.stock.presentation;

import com.NTFOODS.Api_tanty.modules.stock.application.reception.command.CreateReceiptCommand;
import com.NTFOODS.Api_tanty.modules.stock.application.reception.command.ValidateReceiptCommand;
import com.NTFOODS.Api_tanty.modules.stock.application.reception.handler.CreateReceiptHandler;
import com.NTFOODS.Api_tanty.modules.stock.application.reception.handler.ValidateReceiptHandler;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceiptStatus;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject.ReceiptNumber;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * ReceiptController - Controller REST pour la gestion des réceptions
 * Fournit les endpoints pour créer et valider les réceptions fournisseurs et de production
 */
@RestController
@RequestMapping("/api/v1/stock/receipts")
@Tag(name = "Stock Receipts", description = "API pour la gestion des réceptions - Réception fournisseur et production")
@SecurityRequirement(name = "bearerAuth")
public class ReceiptController {

    private final CreateReceiptHandler createReceiptHandler;
    private final ValidateReceiptHandler validateReceiptHandler;

    public ReceiptController(CreateReceiptHandler createReceiptHandler, ValidateReceiptHandler validateReceiptHandler) {
        this.createReceiptHandler = createReceiptHandler;
        this.validateReceiptHandler = validateReceiptHandler;
    }

    /**
     * Endpoint pour créer une nouvelle réception
     * @param command Commande de création de réception
     * @return Numéro de la réception créée
     */
    @PostMapping
    @Operation(
        summary = "Créer une réception",
        description = "Crée une nouvelle réception (fournisseur ou production) avec les articles à recevoir"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Réception créée avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReceiptNumber.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non authentifié - Token JWT manquant ou invalide",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Accès refusé - Rôle insuffisant (nécessite ROLE_STOCK ou ROLE_ADMIN)",
            content = @Content
        )
    })
    @PreAuthorize("hasAuthority('ROLE_STOCK') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ReceiptNumber> createReceipt(@Valid @RequestBody CreateReceiptCommand command) {
        ReceiptNumber receiptNumber = createReceiptHandler.handle(command);
        return ResponseEntity.ok(receiptNumber);
    }

    /**
     * Endpoint pour valider une réception (première ou seconde validation)
     * @param command Commande de validation de réception
     * @return Réponse vide si succès
     */
    @PostMapping("/validate")
    @Operation(
        summary = "Valider une réception",
        description = "Valide une réception (première validation par gestionnaire stock, seconde validation par chef prod ou contrôle général)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Réception validée avec succès",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non authentifié - Token JWT manquant ou invalide",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Accès refusé - Rôle insuffisant",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Requête invalide - Statut de réception incorrect ou code d'autorisation manquant",
            content = @Content
        )
    })
    @PreAuthorize("hasAuthority('ROLE_STOCK') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_PRODUCTION')")
    public ResponseEntity<Void> validateReceipt(@Valid @RequestBody ValidateReceiptCommand command) {
        validateReceiptHandler.handle(command);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint pour obtenir les réceptions en attente de première validation
     * @return Liste des réceptions en attente
     */
    @GetMapping("/pending-first-validation")
    @Operation(
        summary = "Réceptions en attente de première validation",
        description = "Récupère toutes les réceptions en attente de première validation par le gestionnaire de stock"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Réceptions récupérées avec succès",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non authentifié - Token JWT manquant ou invalide",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Accès refusé - Rôle insuffisant",
            content = @Content
        )
    })
    @PreAuthorize("hasAuthority('ROLE_STOCK') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> getPendingFirstValidation() {
        // TODO: Implémenter la récupération des réceptions en attente
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint pour obtenir les réceptions en attente de seconde validation
     * @return Liste des réceptions en attente
     */
    @GetMapping("/pending-second-validation")
    @Operation(
        summary = "Réceptions en attente de seconde validation",
        description = "Récupère toutes les réceptions en attente de seconde validation par le chef prod ou contrôle général"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Réceptions récupérées avec succès",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non authentifié - Token JWT manquant ou invalide",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Accès refusé - Rôle insuffisant",
            content = @Content
        )
    })
    @PreAuthorize("hasAuthority('ROLE_PRODUCTION') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> getPendingSecondValidation() {
        // TODO: Implémenter la récupération des réceptions en attente
        return ResponseEntity.ok().build();
    }
}
