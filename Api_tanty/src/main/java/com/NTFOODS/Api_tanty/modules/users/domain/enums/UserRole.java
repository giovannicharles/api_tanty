package com.NTFOODS.Api_tanty.modules.users.domain.enums;

/**
 * UserRole - Énumération des rôles utilisateurs dans l'ERP TANTY
 * Chaque rôle correspond à un module fonctionnel de l'entreprise
 */
public enum UserRole {

    /**
     * ROLE_ADMIN - Administrateur système
     * Accès complet à tous les modules
     */
    ROLE_ADMIN,

    /**
     * ROLE_STOCK - Gestionnaire de stock
     * Accès au module Stock: réceptions, validations, inventaires, mouvements
     */
    ROLE_STOCK,

    /**
     * ROLE_COMMERCIAL - Responsable commercial
     * Accès au module Commercial: ventes, dotations, caisse
     */
    ROLE_COMMERCIAL,

    /**
     * ROLE_PRODUCTION - Responsable de production
     * Accès au module Production: lots de production, commandes internes
     */
    ROLE_PRODUCTION,

    /**
     * ROLE_FINANCE - Responsable financier
     * Accès au module Finance: facturation, comptabilité, rapports financiers
     */
    ROLE_FINANCE,

    /**
     * ROLE_RH - Responsable des ressources humaines
     * Accès au module RH: gestion des employés, congés, paie
     */
    ROLE_RH,

    /**
     * ROLE_DIRECTION - Direction générale
     * Accès en lecture seule à tous les modules pour supervision
     */
    ROLE_DIRECTION,

    /**
     * ROLE_CAISSIER - Caissier
     * Accès limité au module Commercial pour les transactions de caisse
     */
    ROLE_CAISSIER,

    /**
     * ROLE_MAGASINIER - Magasinier
     * Accès limité au module Stock pour les opérations d'entrée/sortie
     */
    ROLE_MAGASINIER,

    /**
     * ROLE_VALIDATEUR - Validateur
     * Rôle spécial pour valider les réceptions et commandes
     */
    ROLE_VALIDATEUR
}
