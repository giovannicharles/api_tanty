package com.NTFOODS.Api_tanty.shared.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * PageResponse - DTO pour les réponses paginées
 * Contient les données paginées avec des métadonnées sur la pagination
 */
public class PageResponse<T> {

    // Liste des éléments de la page courante
    private List<T> content;

    // Numéro de la page courante
    private int currentPage;

    // Taille de la page
    private int pageSize;

    // Nombre total d'éléments
    private long totalElements;

    // Nombre total de pages
    private int totalPages;

    // Indique s'il y a une page précédente
    private boolean hasPrevious;

    // Indique s'il y a une page suivante
    private boolean hasNext;

    /**
     * Constructeur par défaut
     */
    public PageResponse() {
    }

    /**
     * Constructeur complet
     * @param content Liste des éléments de la page courante
     * @param currentPage Numéro de la page courante
     * @param pageSize Taille de la page
     * @param totalElements Nombre total d'éléments
     */
    public PageResponse(List<T> content, int currentPage, int pageSize, long totalElements) {
        this.content = content;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
        this.hasPrevious = currentPage > 0;
        this.hasNext = currentPage < totalPages - 1;
    }

    // Getters et Setters

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    @JsonProperty("contenu")
    public List<T> getContenu() { return content; }

    @JsonProperty("pageCourante")
    public int getPageCourante() { return currentPage; }

    @JsonProperty("taillePage")
    public int getTaillePage() { return pageSize; }

    @JsonProperty("dernierePage")
    public boolean isDernierePage() { return !hasNext; }
}
