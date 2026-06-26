package com.NTFOODS.Api_tanty.modules.users.infrastructure.persistence.user.repository;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.NTFOODS.Api_tanty.modules.users.domain.aggregate.UserAggregate;
import com.NTFOODS.Api_tanty.modules.users.domain.repository.UserRepository;
import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.PhoneNumber;
import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.UserMatricule;
import com.NTFOODS.Api_tanty.modules.users.infrastructure.persistence.user.jpa.UserJpaRepository;
import com.NTFOODS.Api_tanty.modules.users.infrastructure.persistence.user.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

/**
 * UserRepositoryImpl - Implémentation JPA de UserRepository
 * Utilise UserJpaRepository pour accéder aux données et UserMapper pour les conversions
 */
@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    // Repository JPA pour accéder aux données
    private final UserJpaRepository jpaRepository;

    /**
     * Trouve un utilisateur par son matricule
     * @param matricule Matricule de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    @Override
    public Optional<UserAggregate> findByMatricule(UserMatricule matricule) {
        return jpaRepository.findByMatricule(matricule.value())
            .map(UserMapper::toDomain);
    }

    /**
     * Trouve un utilisateur par son numéro de téléphone
     * @param phone Numéro de téléphone de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    @Override
    public Optional<UserAggregate> findByPhone(PhoneNumber phone) {
        return jpaRepository.findByPhone(phone.getNumber())
            .map(UserMapper::toDomain);
    }

    /**
     * Sauvegarde un utilisateur
     * @param user Utilisateur à sauvegarder
     */
    @Override
    public void save(UserAggregate user) {
        jpaRepository.save(UserMapper.toEntity(user));
    }

    /**
     * Compte le nombre total d'utilisateurs
     * @return Nombre total d'utilisateurs
     */
    @Override
    public long count() {
        return jpaRepository.count();
    }
}
