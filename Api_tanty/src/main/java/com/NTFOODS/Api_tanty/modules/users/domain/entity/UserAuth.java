package com.NTFOODS.Api_tanty.modules.users.domain.entity;

import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.EmailAddress;
import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.PasswordHash;

/**
 * UserAuth - Entité d'authentification utilisateur
 * Contient les informations de sécurité et d'accès de l'utilisateur
 */
public class UserAuth {

    // Adresse email de l'utilisateur (optionnel)
    private EmailAddress email;

    // Mot de passe haché de l'utilisateur
    private PasswordHash password;

    // Indicateur de verrouillage du compte
    private boolean locked;

    // Nombre de tentatives de connexion échouées
    private Integer failedAttempts;

    // Indicateur d'activation de l'authentification à deux facteurs
    private boolean twoFactorEnabled;

    /**
     * Constructeur par défaut
     */
    public UserAuth() {
        this.locked = false;
        this.failedAttempts = 0;
        this.twoFactorEnabled = false;
    }

    /**
     * Constructeur avec mot de passe haché
     * @param password Mot de passe haché
     */
    public UserAuth(PasswordHash password) {
        this();
        this.password = password;
    }

    /**
     * Enregistre une tentative de connexion échouée
     * Incrémente le compteur et verrouille le compte après 5 échecs
     */
    public void registerFailure(){
        // Incrémentation du compteur d'échecs
        failedAttempts++;

        // Vérification si le nombre d'échecs atteint 5
        if(failedAttempts >= 5){
            // Verrouillage du compte
            locked = true;
        }
    }

    /**
     * Réinitialise le compteur de tentatives échouées
     * Appelé après une connexion réussie
     */
    public void resetFailures(){
        // Réinitialisation du compteur à zéro
        failedAttempts = 0;
    }

    /**
     * Vérifie si le compte est verrouillé
     * @return true si le compte est verrouillé, false sinon
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Définit le statut de verrouillage du compte
     * @param locked true pour verrouiller, false pour déverrouiller
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Retourne le mot de passe haché
     * @return Mot de passe haché
     */
    public PasswordHash getPassword() {
        return password;
    }

    /**
     * Définit le mot de passe haché
     * @param password Nouveau mot de passe haché
     */
    public void setPassword(PasswordHash password) {
        this.password = password;
    }

    /**
     * Retourne l'adresse email
     * @return Adresse email
     */
    public EmailAddress getEmail() {
        return email;
    }

    /**
     * Définit l'adresse email
     * @param email Nouvelle adresse email
     */
    public void setEmail(EmailAddress email) {
        this.email = email;
    }

    /**
     * Retourne le nombre de tentatives échouées
     * @return Nombre de tentatives échouées
     */
    public Integer getFailedAttempts() {
        return failedAttempts;
    }

    /**
     * Définit le nombre de tentatives échouées
     * @param failedAttempts Nouveau nombre de tentatives
     */
    public void setFailedAttempts(Integer failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    /**
     * Vérifie si l'authentification à deux facteurs est activée
     * @return true si 2FA est activé, false sinon
     */
    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    /**
     * Définit le statut de l'authentification à deux facteurs
     * @param twoFactorEnabled true pour activer, false pour désactiver
     */
    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }
}
