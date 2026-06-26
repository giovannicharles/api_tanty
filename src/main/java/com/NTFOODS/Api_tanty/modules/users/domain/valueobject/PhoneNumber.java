package com.NTFOODS.Api_tanty.modules.users.domain.valueobject;

import java.util.Objects;

/**
 * PhoneNumber - Value object pour le numéro de téléphone
 * Représente un numéro de téléphone valide
 */
public class PhoneNumber {
    
    // Numéro de téléphone
    private final String number;

    /**
     * Constructeur par défaut
     */
    public PhoneNumber() {
        this.number = null;
    }

    /**
     * Constructeur avec numéro de téléphone
     * @param number Numéro de téléphone
     */
    public PhoneNumber(String number) {
        // Validation basique du numéro de téléphone
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Le numéro de téléphone ne peut pas être vide");
        }
        this.number = number;
    }

    /**
     * Retourne le numéro de téléphone
     * @return Numéro de téléphone
     */
    public String getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneNumber that = (PhoneNumber) o;
        return Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        return number;
    }
}
