package com.NTFOODS.Api_tanty.modules.users.domain.aggregate;

import java.time.LocalDate;

import org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor;

import com.NTFOODS.Api_tanty.modules.users.domain.entity.UserAuth;
import com.NTFOODS.Api_tanty.modules.users.domain.entity.UserProfile;

import com.NTFOODS.Api_tanty.modules.users.domain.enums.UserRole;
import com.NTFOODS.Api_tanty.modules.users.domain.enums.UserStatus;
import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.UserMatricule;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
/**
 * UserAggregate - Agrégat racine pour le domaine User
 * Contient toutes les entités et valeurs liées à un utilisateur
 */
public class UserAggregate {

    // Identifiant unique de l'utilisateur
    private final UserId id;

    // Matricule unique de l'utilisateur
    private final UserMatricule matricule;

    // Profil de l'utilisateur (informations personnelles)
    private UserProfile profile;

    // Informations d'authentification de l'utilisateur
    private UserAuth auth;

    // Rôle de l'utilisateur dans le système
    private UserRole role;

    // Statut de l'utilisateur (ACTIF, INACTIF, BLOQUÉ)
    private UserStatus status;

    /**
     * Constructeur de l'agrégat utilisateur
     * @param id Identifiant unique
     * @param matricule Matricule unique
     * @param profile Profil utilisateur
     * @param auth Informations d'authentification
     * @param role Rôle de l'utilisateur
     */
    public UserAggregate(
            UserId id,
            UserMatricule matricule,
            UserProfile profile,
            UserAuth auth,
            UserRole role
    ){
        // Initialisation de l'identifiant
        this.id = id;

        // Initialisation du matricule
        this.matricule = matricule;

        // Initialisation du profil
        this.profile = profile;

        // Initialisation des informations d'authentification
        this.auth = auth;

        // Initialisation du rôle
        this.role = role;

        // Définition du statut par défaut à ACTIF
        this.status = UserStatus.ACTIVE;
    }

    // Getters pour tous les champs

    /**
     * Retourne l'identifiant de l'utilisateur
     * @return Identifiant unique
     */
    public UserId getId() {
        return id;
    }

    /**
     * Retourne le matricule de l'utilisateur
     * @return Matricule unique
     */
    public UserMatricule getMatricule() {
        return matricule;
    }

    /**
     * Retourne le profil de l'utilisateur
     * @return Profil utilisateur
     */
    public UserProfile getProfile() {
        return profile;
    }

    /**
     * Définit le profil de l'utilisateur
     * @param profile Nouveau profil
     */
    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    /**
     * Retourne les informations d'authentification
     * @return Informations d'authentification
     */
    public UserAuth getAuth() {
        return auth;
    }

    /**
     * Définit les informations d'authentification
     * @param auth Nouvelles informations d'authentification
     */
    public void setAuth(UserAuth auth) {
        this.auth = auth;
    }

    /**
     * Retourne le rôle de l'utilisateur
     * @return Rôle de l'utilisateur
     */
    public UserRole getRole() {
        return role;
    }

    /**
     * Définit le rôle de l'utilisateur
     * @param role Nouveau rôle
     */
    public void setRole(UserRole role) {
        this.role = role;
    }

    /**
     * Retourne le statut de l'utilisateur
     * @return Statut de l'utilisateur
     */
    public UserStatus getStatus() {
        return status;
    }

    /**
     * Définit le statut de l'utilisateur
     * @param status Nouveau statut
     */
    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
