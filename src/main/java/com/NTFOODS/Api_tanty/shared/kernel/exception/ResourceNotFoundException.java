package com.NTFOODS.Api_tanty.shared.kernel.exception;

/** Levée quand une ressource demandée n'existe pas (mappée en HTTP 404). */
public class ResourceNotFoundException extends DomainException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
