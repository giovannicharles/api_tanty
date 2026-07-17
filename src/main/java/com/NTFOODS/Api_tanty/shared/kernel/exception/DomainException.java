package com.NTFOODS.Api_tanty.shared.kernel.exception;

/**
 * DomainException - Exception de base pour toutes les erreurs métier du domaine.
 * Remplace l'ancien stub vide qui ne pouvait pas être levé (n'étendait pas Throwable).
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
