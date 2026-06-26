package com.NTFOODS.Api_tanty.modules.users.infrastructure.persistence.user.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.NTFOODS.Api_tanty.modules.users.domain.aggregate.UserAggregate;
import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.UserMatricule;

/**
 * UserJpaRepository - Interface Spring Data JPA pour l'entité UserJpaEntity
 * Fournit les méthodes d'accès aux données pour les utilisateurs
 */
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    /**
     * Trouve un utilisateur par son matricule
     * @param matricule Matricule de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    Optional<UserJpaEntity> findByMatricule(String matricule);

    /**
     * Trouve un utilisateur par son numéro de téléphone
     * @param phone Numéro de téléphone de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    Optional<UserJpaEntity> findByPhone(String phone);

    /**
     * Compte le nombre total d'utilisateurs
     * @return Nombre total d'utilisateurs
     */
    long count();
    Optional<UserJpaEntity> findFirstByMatricule(String matricule);
}
