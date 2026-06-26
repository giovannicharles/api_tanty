package com.NTFOODS.Api_tanty.modules.users.application.dto;

import com.NTFOODS.Api_tanty.modules.users.domain.enums.UserRole;
import com.NTFOODS.Api_tanty.modules.users.domain.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * UserResponse - DTO pour la réponse utilisateur
 * Contient toutes les informations d'un utilisateur pour l'exposition via l'API
 */
@Schema(description = "Réponse contenant les informations complètes d'un utilisateur")
public record UserResponse(

        @Schema(description = "Identifiant unique de l'utilisateur (matricule)", example = "USR-0001")
        String id,

        @Schema(description = "Matricule de l'utilisateur", example = "USR-0001")
        String matricule,

        @Schema(description = "Prénom de l'utilisateur", example = "Jean")
        String firstname,

        @Schema(description = "Nom de l'utilisateur", example = "Charles")
        String lastname,

        @Schema(description = "Numéro de téléphone", example = "+261 34 00 000 00")
        String phone,

        @Schema(description = "Adresse email", example = "jean.charles@tanty.com")
        String email,

        @Schema(description = "Adresse de résidence", example = "Antananarivo, Madagascar")
        String address,

        @Schema(description = "Numéro de la Carte Nationale d'Identité", example = "123456789012")
        String cni,

        @Schema(description = "Date de naissance", example = "1990-01-15")
        LocalDate dateOfBirth,

        @Schema(description = "Lieu de naissance", example = "Antananarivo")
        String placeOfBirth,

        @Schema(description = "Nationalité", example = "Malagasy")
        String nationality,

        @Schema(description = "Niveau hiérarchique", example = "Senior")
        String level,

        @Schema(description = "Statut matrimonial", example = "Marié")
        String maritalStatus,

        @Schema(description = "Rôle de l'utilisateur dans le système", example = "ROLE_STOCK")
        UserRole role,

        @Schema(description = "Statut du compte utilisateur", example = "ACTIVE")
        UserStatus status,

        @Schema(description = "Date et heure de la dernière connexion", example = "2026-06-17T14:30:00")
        LocalDateTime lastLogin,

        @Schema(description = "Date de création du compte", example = "2026-01-01T08:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Date de dernière mise à jour", example = "2026-06-17T14:30:00")
        LocalDateTime updatedAt
) {}
