package com.NTFOODS.Api_tanty.modules.users.domain.repository;

import java.util.Optional;

import com.NTFOODS.Api_tanty.modules.users.domain.aggregate.UserAggregate;
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

    Optional<UserAggregate> findFirstByMatricule(UserMatricule matricule);
}
