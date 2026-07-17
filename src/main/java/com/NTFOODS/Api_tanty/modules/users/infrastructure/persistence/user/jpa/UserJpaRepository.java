package com.NTFOODS.Api_tanty.modules.users.infrastructure.persistence.user.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserJpaRepository - Interface Spring Data JPA pour l'entité UserJpaEntity
 * Fournit les méthodes d'accès aux données pour les utilisateurs
 */
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    /**
     * Trouve tous les utilisateurs partageant un matricule.
     * BUG CORRIGÉ : l'ancienne méthode "Optional<UserJpaEntity> findByMatricule(String)"
     * plantait avec NonUniqueResultException dès qu'un matricule existait en double en
     * base - ce qui était systématique tant que UserRepositoryImpl.save() créait un
     * nouvel utilisateur à chaque appel au lieu de mettre à jour l'existant (bug corrigé
     * séparément, mais les doublons déjà insérés en base restent tant qu'ils ne sont pas
     * nettoyés manuellement). On récupère donc une liste et on choisit le bon candidat
     * dans UserRepositoryImpl plutôt que de laisser Hibernate lever une exception.
     * @param matricule Matricule de l'utilisateur
     * @return Liste des entités correspondantes (normalement une seule)
     */
    List<UserJpaEntity> findAllByMatricule(String matricule);

    /**
     * Trouve tous les utilisateurs partageant un numéro de téléphone (même raison que
     * findAllByMatricule ci-dessus).
     * @param phone Numéro de téléphone de l'utilisateur
     * @return Liste des entités correspondantes (normalement une seule)
     */
    List<UserJpaEntity> findAllByPhone(String phone);

    /**
     * Trouve tous les utilisateurs actifs ayant un rôle donné (ex: tous les gestionnaires
     * de stock, tous les comptables). Utilisé pour le routage des validations et notifications.
     * @param role Nom du rôle (ex: "ROLE_STOCK")
     * @return Liste des entités utilisateur correspondantes, statut ACTIVE uniquement
     */
    List<UserJpaEntity> findByRoleAndStatus(String role, String status);

    default List<UserJpaEntity> findByRole(String role) {
        return findByRoleAndStatus(role, "ACTIVE");
    }

    /**
     * Compte le nombre total d'utilisateurs
     * @return Nombre total d'utilisateurs
     */
    long count();
}
