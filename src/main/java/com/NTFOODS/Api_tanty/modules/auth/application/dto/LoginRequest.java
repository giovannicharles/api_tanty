package com.NTFOODS.Api_tanty.modules.auth.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * LoginRequest - DTO pour les données de connexion
 * Contient le matricule et le mot de passe de l'utilisateur
 */
@Schema(description = "Données de connexion utilisateur")
public class LoginRequest {

    // Matricule de l'utilisateur
    @NotBlank(message = "Le matricule est obligatoire")
    @Schema(
        description = "Matricule unique de l'utilisateur pour l'authentification",
        example = "ADM-2025-0001"
    )
    private String matricule;

    // Mot de passe de l'utilisateur
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Schema(
        description = "Mot de passe de l'utilisateur (doit correspondre au mot de passe haché en base de données)",
        example = "MonMotDePasse123!",
        format = "password"
    )
    private String password;

    /**
     * Constructeur par défaut
     */
    public LoginRequest() {
    }

    /**
     * Constructeur complet
     * @param matricule Matricule de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     */
    public LoginRequest(String matricule, String password) {
        this.matricule = matricule;
        this.password = password;
    }

    // Getters et Setters

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
