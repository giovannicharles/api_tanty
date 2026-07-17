package com.NTFOODS.Api_tanty.shared.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean succes;
    private String message;
    private T donnees;
    private String erreur;
    private LocalDateTime horodatage;

    private ApiResponse() {
        this.horodatage = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> succes(T donnees, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.succes = true;
        response.message = message;
        response.donnees = donnees;
        return response;
    }

    public static <T> ApiResponse<T> succes(T donnees) {
        return succes(donnees, "Opération réussie");
    }

    public static <T> ApiResponse<T> erreur(String erreur) {
        ApiResponse<T> response = new ApiResponse<>();
        response.succes = false;
        response.erreur = erreur;
        return response;
    }

    public static <T> ApiResponse<T> erreur(String message, String erreur) {
        ApiResponse<T> response = new ApiResponse<>();
        response.succes = false;
        response.message = message;
        response.erreur = erreur;
        return response;
    }

    public boolean isSucces() { return succes; }
    public String getMessage() { return message; }
    public T getDonnees() { return donnees; }
    public String getErreur() { return erreur; }
    public LocalDateTime getHorodatage() { return horodatage; }
}
