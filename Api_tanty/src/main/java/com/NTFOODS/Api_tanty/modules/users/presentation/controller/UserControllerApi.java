package com.NTFOODS.Api_tanty.modules.users.presentation.controller;

import com.NTFOODS.Api_tanty.modules.users.application.create.command.CreateUserCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * UserControllerApi - Interface API pour la gestion des utilisateurs
 * Définit les endpoints pour la création et la gestion des utilisateurs
 */
@RequestMapping("/api/v1/users")
@Tag(name = "Gestion des utilisateurs", description = "API pour la gestion des utilisateurs du système ERP")
@SecurityRequirement(name = "bearerAuth")
public interface UserControllerApi {

    /**
     * Endpoint pour créer un nouvel utilisateur
     * @param cmd Commande de création d'utilisateur avec toutes les informations requises
     */
    @PostMapping
    @Operation(
        summary = "Créer un nouvel utilisateur",
        description = "Crée un nouvel utilisateur dans le système avec toutes les informations requises (profil, authentification, rôle, etc.). Le matricule est généré automatiquement."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Utilisateur créé avec succès",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Données de création invalides - Champs manquants ou incorrects",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non authentifié - Token JWT manquant ou invalide",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Accès refusé - Rôle insuffisant (nécessite ROLE_ADMIN ou ROLE_RH)",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflit - Utilisateur avec ce matricule existe déjà",
            content = @Content
        )
    })
    void create(
        @Parameter(
            description = "Commande de création d'utilisateur avec toutes les informations requises",
            required = true,
            schema = @Schema(implementation = CreateUserCommand.class)
        )
        @RequestBody CreateUserCommand cmd
    );
}
