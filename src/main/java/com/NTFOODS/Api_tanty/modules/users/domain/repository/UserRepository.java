package com.NTFOODS.Api_tanty.modules.users.domain.repository;

import java.util.List;
import java.util.Optional;

import com.NTFOODS.Api_tanty.modules.users.domain.aggregate.UserAggregate;
import com.NTFOODS.Api_tanty.modules.users.domain.enums.UserRole;
import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.PhoneNumber;
import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.UserMatricule;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

/**
 * UserRepository - Interface du repository pour les utilisateurs
 * Définit les méthodes pour accéder aux données des utilisateurs
 */
public interface UserRepository {

    /**
     * Trouve un utilisateur par son matricule
     * @param matricule Matricule de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    Optional<UserAggregate> findByMatricule(UserMatricule matricule);

    /**
     * Trouve un utilisateur par son numéro de téléphone
     * @param phone Numéro de téléphone de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    Optional<UserAggregate> findByPhone(PhoneNumber phone);

    /**
     * Sauvegarde un utilisateur
     * @param user Utilisateur à sauvegarder
     */
    void save(UserAggregate user);

    /**
     * Compte le nombre total d'utilisateurs
     * @return Nombre total d'utilisateurs
     */
    long count();

    /**
     * Trouve tous les utilisateurs actifs ayant un rôle donné.
     * Utilisé notamment pour notifier les valideurs concernés (ex: tous les ROLE_FINANCE
     * lors de la réception d'une matière première).
     * @param role Rôle recherché
     * @return Liste des utilisateurs ayant ce rôle
     */
    List<UserAggregate> findByRole(UserRole role);
}
