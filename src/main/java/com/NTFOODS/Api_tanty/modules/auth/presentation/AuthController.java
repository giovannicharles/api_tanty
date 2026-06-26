package com.NTFOODS.Api_tanty.modules.auth.presentation;

import com.NTFOODS.Api_tanty.modules.auth.application.dto.AuthResponse;
import com.NTFOODS.Api_tanty.modules.auth.application.dto.LoginRequest;
import com.NTFOODS.Api_tanty.modules.auth.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthController - Controller REST pour l'authentification
 * Fournit les endpoints pour la connexion et déconnexion des utilisateurs
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentification", description = "API pour l'authentification des utilisateurs")
public class AuthController {

    // Service d'authentification pour la logique métier
    private final AuthService authService;

    /**
     * Constructeur avec injection du service d'authentification
     * @param authService Service d'authentification
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint pour la connexion d'un utilisateur
     * @param loginRequest Données de connexion (matricule et mot de passe)
     * @return Réponse contenant le token JWT et les informations utilisateur
     */
    @PostMapping("/login")
    @Operation(
        summary = "Connexion utilisateur",
        description = "Authentifie un utilisateur avec son matricule et mot de passe. Retourne un token JWT valide pour 24 heures."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Connexion réussie - Token JWT généré",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Identifiants invalides - Matricule ou mot de passe incorrect",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Compte verrouillé ou désactivé",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Données de requête invalides",
            content = @Content
        )
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // Appel au service d'authentification pour traiter la connexion
        AuthResponse authResponse = authService.login(loginRequest);

        // Retourner la réponse avec le token JWT
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Endpoint pour la déconnexion d'un utilisateur
     * @param matricule Matricule de l'utilisateur à déconnecter
     * @return Réponse vide si succès
     */
    @PostMapping("/logout")
    @Operation(
        summary = "Déconnexion utilisateur",
        description = "Déconnecte un utilisateur. Note: Avec JWT stateless, la déconnexion est principalement gérée côté client en supprimant le token."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Déconnexion réussie",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non authentifié",
            content = @Content
        )
    })
    public ResponseEntity<Void> logout(@RequestBody String matricule) {
        // Appel au service d'authentification pour traiter la déconnexion
        authService.logout(matricule);

        // Retourner une réponse vide avec code 200
        return ResponseEntity.ok().build();
    }
}
