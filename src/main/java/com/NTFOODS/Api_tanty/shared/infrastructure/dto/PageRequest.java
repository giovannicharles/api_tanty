package com.NTFOODS.Api_tanty.shared.infrastructure.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * PageRequest - DTO pour les paramètres de pagination
 * Permet de spécifier le numéro de page et la taille de la page pour les requêtes paginées
 */
public class PageRequest {

    /**
     * Numéro de la page (commence à 0)
     */
    @NotNull(message = "Le numéro de page est obligatoire")
    @Min(value = 0, message = "Le numéro de page doit être >= 0")
    private Integer page;

    /**
     * Taille de la page (nombre d'éléments par page)
     */
    @NotNull(message = "La taille de la page est obligatoire")
    @Min(value = 1, message = "La taille de la page doit être >= 1")
    @Max(value = 100, message = "La taille de la page doit être <= 100")
    private Integer size;

    /**
     * Constructeur par défaut
     */
    public PageRequest() {
        // Valeurs par défaut
        this.page = 0;
        this.size = 10;
    }

    /**
     * Constructeur complet
     * @param page Numéro de la page
     * @param size Taille de la page
     */
    public PageRequest(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    // Getters et Setters

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * Calcule l'offset pour la requête SQL
     * @return Offset (nombre d'éléments à sauter)
     */
    public int getOffset() {
        return page * size;
    }
}
