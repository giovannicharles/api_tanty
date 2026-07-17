package com.NTFOODS.Api_tanty.shared.kernel.exception;

/** Levée quand une opération est demandée alors que l'entité n'est pas dans l'état requis (HTTP 409). */
public class InvalidStateException extends DomainException {
    public InvalidStateException(String message) {
        super(message);
    }
}
