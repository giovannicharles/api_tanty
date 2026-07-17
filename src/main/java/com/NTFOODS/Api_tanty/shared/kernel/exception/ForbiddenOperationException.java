package com.NTFOODS.Api_tanty.shared.kernel.exception;

/**
 * Levée quand l'utilisateur authentifié n'a pas le rôle requis pour effectuer
 * l'opération métier demandée (ex: seul le Comptable peut faire la seconde
 * validation d'une réception de matière première). Mappée en HTTP 403.
 */
public class ForbiddenOperationException extends DomainException {
    public ForbiddenOperationException(String message) {
        super(message);
    }
}
