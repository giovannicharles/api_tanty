package com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums;

/**
 * ReceptionType - Type de réception, détermine le circuit de double validation
 * applicable (cf. cahier des charges NT Foods §3).
 *
 * CONSOMMABLE     : Gestionnaire de stock (1ère) -> Contrôleur Général (2nde)
 * MATIERE_PREMIERE: Gestionnaire de stock (1ère) -> Comptable (2nde)
 * MATERIEL        : Gestionnaire de stock (1ère) -> Contrôleur Général (2nde)
 *
 * Note: Les produits finis ne passent PLUS par la réception. Ils entrent en stock
 * via la déclaration des lots de production (flux séparé).
 */
public enum ReceptionType {
    CONSOMMABLE,
    MATIERE_PREMIERE,
    MATERIEL,
    PRODUIT_FINI
}
