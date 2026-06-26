package com.NTFOODS.Api_tanty.modules.stock.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * MaterielDTO - DTO pour les matériels
 * Contient les informations d'un matériel dans le système
 */
@Schema(description = "Données d'un matériel")
public class MaterielDTO {

    @Schema(description = "Identifiant unique du matériel")
    private String id;

    @NotBlank(message = "Le code est obligatoire")
    @Schema(description = "Code unique du matériel", example = "MAT-001")
    private String code;

    @NotBlank(message = "Le nom est obligatoire")
    @Schema(description = "Nom du matériel", example = "Perceuse à colonne")
    private String nom;

    @NotBlank(message = "La catégorie est obligatoire")
    @Schema(description = "Catégorie du matériel", example = "OUTILLAGE")
    private String categorie;

    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    @Schema(description = "Quantité disponible", example = "1")
    private Integer quantite;

    @NotBlank(message = "L'unité est obligatoire")
    @Schema(description = "Unité de mesure", example = "unité")
    private String unite;

    @NotBlank(message = "L'emplacement est obligatoire")
    @Schema(description = "Emplacement du matériel", example = "Atelier A")
    private String emplacement;

    @NotBlank(message = "L'état est obligatoire")
    @Schema(description = "État du matériel", example = "DISPONIBLE")
    private String etat;

    @Schema(description = "Date d'acquisition", example = "2024-01-15")
    private String dateAcquisition;

    @Schema(description = "Fournisseur du matériel", example = "Bosch")
    private String fournisseur;

    @NotNull(message = "Le coût d'acquisition est obligatoire")
    @Positive(message = "Le coût d'acquisition doit être positif")
    @Schema(description = "Coût d'acquisition en CFA", example = "150000")
    private Double coutAcquisition;

    @Schema(description = "Date de la dernière maintenance", example = "2024-05-10")
    private String derniereMaintenance;

    @Schema(description = "Date de la prochaine maintenance", example = "2024-11-10")
    private String prochaineMaintenance;

    // Constructeurs
    public MaterielDTO() {
    }

    public MaterielDTO(String id, String code, String nom, String categorie, Integer quantite, 
                       String unite, String emplacement, String etat, String dateAcquisition, 
                       String fournisseur, Double coutAcquisition, String derniereMaintenance, 
                       String prochaineMaintenance) {
        this.id = id;
        this.code = code;
        this.nom = nom;
        this.categorie = categorie;
        this.quantite = quantite;
        this.unite = unite;
        this.emplacement = emplacement;
        this.etat = etat;
        this.dateAcquisition = dateAcquisition;
        this.fournisseur = fournisseur;
        this.coutAcquisition = coutAcquisition;
        this.derniereMaintenance = derniereMaintenance;
        this.prochaineMaintenance = prochaineMaintenance;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getDateAcquisition() {
        return dateAcquisition;
    }

    public void setDateAcquisition(String dateAcquisition) {
        this.dateAcquisition = dateAcquisition;
    }

    public String getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(String fournisseur) {
        this.fournisseur = fournisseur;
    }

    public Double getCoutAcquisition() {
        return coutAcquisition;
    }

    public void setCoutAcquisition(Double coutAcquisition) {
        this.coutAcquisition = coutAcquisition;
    }

    public String getDerniereMaintenance() {
        return derniereMaintenance;
    }

    public void setDerniereMaintenance(String derniereMaintenance) {
        this.derniereMaintenance = derniereMaintenance;
    }

    public String getProchaineMaintenance() {
        return prochaineMaintenance;
    }

    public void setProchaineMaintenance(String prochaineMaintenance) {
        this.prochaineMaintenance = prochaineMaintenance;
    }
}
