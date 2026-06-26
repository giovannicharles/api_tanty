package com.NTFOODS.Api_tanty.modules.auth.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * AuthResponse - DTO pour la réponse d'authentification
 * Contient le token JWT et les informations de l'utilisateur
 */
@Schema(description = "Réponse d'authentification contenant le token JWT et les informations utilisateur")
public class AuthResponse {

    // Token JWT pour l'authentification
    @Schema(
        description = "Token JWT d'authentification valide pour 24 heures",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
    )
    private String token;

    // Matricule de l'utilisateur
    @Schema(
        description = "Matricule unique de l'utilisateur",
        example = "ADM-2025-0001"
    )
    private String matricule;

    // Prénom de l'utilisateur
    @Schema(
        description = "Prénom de l'utilisateur",
        example = "Jean"
    )
    private String firstname;

    // Nom de l'utilisateur
    @Schema(
        description = "Nom de l'utilisateur",
        example = "Dupont"
    )
    private String lastname;

    // Rôle de l'utilisateur
    @Schema(
        description = "Rôle de l'utilisateur dans le système",
        example = "ROLE_ADMIN",
        allowableValues = {"ROLE_ADMIN", "ROLE_STOCK", "ROLE_COMMERCIAL", "ROLE_PRODUCTION", "ROLE_FINANCE", "ROLE_RH", "ROLE_DIRECTION"}
    )
    private String role;

    /**
     * Constructeur par défaut
     */
    public AuthResponse() {
    }

    /**
     * Constructeur complet
     * @param token Token JWT
     * @param matricule Matricule de l'utilisateur
     * @param firstname Prénom de l'utilisateur
     * @param lastname Nom de l'utilisateur
     * @param role Rôle de l'utilisateur
     */
    public AuthResponse(String token, String matricule, String firstname, String lastname, String role) {
        this.token = token;
        this.matricule = matricule;
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = role;
    }

    // Getters et Setters

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
