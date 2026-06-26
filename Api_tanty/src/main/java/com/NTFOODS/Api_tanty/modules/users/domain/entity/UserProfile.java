package com.NTFOODS.Api_tanty.modules.users.domain.entity;

import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.PhoneNumber;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * UserProfile - Profil utilisateur étendu pour l'ERP TANTY
 * Contient toutes les informations personnelles et professionnelles d'un employé
 */
public class UserProfile {

    // Prénom de l'employé
    private String firstname;

    // Nom de famille de l'employé
    private String lastname;

    // Numéro de téléphone de l'employé
    private PhoneNumber phone;

    // Adresse de résidence de l'employé
    private String address;

    // Numéro de la Carte Nationale d'Identité (CNI)
    private String cni;

    // Date de naissance de l'employé
    private LocalDate dateOfBirth;

    // Lieu de naissance de l'employé
    private String placeOfBirth;

    // Nationalité de l'employé
    private String nationality;

    // Niveau hiérarchique dans l'entreprise
    private String level;

    // Statut matrimonial (Célibataire, Marié, Divorcé, Veuf)
    private String maritalStatus;

    // Date et heure de la dernière connexion
    private LocalDateTime lastLogin;

    // Date de création du profil
    private LocalDateTime createdAt;

    // Date de mise à jour du profil
    private LocalDateTime updatedAt;

    /**
     * Constructeur complet du profil utilisateur
     * @param firstname Prénom
     * @param lastname Nom
     * @param phone Numéro de téléphone
     * @param address Adresse
     * @param cni Numéro CNI
     * @param dateOfBirth Date de naissance
     * @param placeOfBirth Lieu de naissance
     * @param nationality Nationalité
     * @param level Niveau hiérarchique
     * @param maritalStatus Statut matrimonial
     */
    public UserProfile(
            String firstname,
            String lastname,
            PhoneNumber phone,
            String address,
            String cni,
            LocalDate dateOfBirth,
            String placeOfBirth,
            String nationality,
            String level,
            String maritalStatus
    ){
        // Initialisation du prénom
        this.firstname = firstname;

        // Initialisation du nom
        this.lastname = lastname;

        // Initialisation du téléphone
        this.phone = phone;

        // Initialisation de l'adresse
        this.address = address;

        // Initialisation du numéro CNI
        this.cni = cni;

        // Initialisation de la date de naissance
        this.dateOfBirth = dateOfBirth;

        // Initialisation du lieu de naissance
        this.placeOfBirth = placeOfBirth;

        // Initialisation de la nationalité
        this.nationality = nationality;

        // Initialisation du niveau
        this.level = level;

        // Initialisation du statut matrimonial
        this.maritalStatus = maritalStatus;

        // Initialisation de la date de création
        this.createdAt = LocalDateTime.now();

        // Initialisation de la date de mise à jour
        this.updatedAt = LocalDateTime.now();
    }

    // Getters et Setters pour tous les champs

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
        this.updatedAt = LocalDateTime.now();
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
        this.updatedAt = LocalDateTime.now();
    }

    public PhoneNumber getPhone() {
        return phone;
    }

    public void setPhone(PhoneNumber phone) {
        this.phone = phone;
        this.updatedAt = LocalDateTime.now();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }

    public String getCni() {
        return cni;
    }

    public void setCni(String cni) {
        this.cni = cni;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
        this.updatedAt = LocalDateTime.now();
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
        this.updatedAt = LocalDateTime.now();
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
        this.updatedAt = LocalDateTime.now();
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Met à jour la date de dernière connexion
     */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
}
