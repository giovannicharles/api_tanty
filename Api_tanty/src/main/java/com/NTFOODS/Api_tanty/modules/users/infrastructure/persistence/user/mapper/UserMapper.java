package com.NTFOODS.Api_tanty.modules.users.infrastructure.persistence.user.mapper;

import com.NTFOODS.Api_tanty.modules.users.domain.aggregate.UserAggregate;
import com.NTFOODS.Api_tanty.modules.users.domain.entity.UserAuth;
import com.NTFOODS.Api_tanty.modules.users.domain.entity.UserProfile;
import com.NTFOODS.Api_tanty.modules.users.domain.enums.UserRole;
import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.PasswordHash;
import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.PhoneNumber;
import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.UserMatricule;
import com.NTFOODS.Api_tanty.modules.users.infrastructure.persistence.user.jpa.UserJpaEntity;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

/**
 * UserMapper - Mapper pour convertir entre UserJpaEntity et UserAggregate
 * Gère la conversion entre l'entité JPA et l'agrégat de domaine
 */
public class UserMapper {

    /**
     * Convertit UserJpaEntity en UserAggregate
     * @param entity Entité JPA
     * @return Agrégat de domaine
     */
    public static UserAggregate toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        // Créer le profil utilisateur
        UserProfile profile = new UserProfile(
            entity.getFirstname(),
            entity.getLastname(),
            new PhoneNumber(entity.getPhone()),
            null, // address
            null, // cni
            null, // dateOfBirth
            null, // placeOfBirth
            null, // nationality
            null, // level
            null  // maritalStatus
        );

        // Créer les informations d'authentification
        UserAuth auth = new UserAuth();
        auth.setPassword(new PasswordHash(entity.getPassword()));
        auth.setLocked(false);
        auth.setFailedAttempts(0);
        auth.setTwoFactorEnabled(false);

        // Créer le matricule
        UserMatricule matricule = new UserMatricule(entity.getMatricule());

        // Créer l'agrégat utilisateur
        return new UserAggregate(
            new UserId(entity.getMatricule()),
            matricule,
            profile,
            auth,
            UserRole.valueOf(entity.getRole())
        );
    }

    /**
     * Convertit UserAggregate en UserJpaEntity
     * @param aggregate Agrégat de domaine
     * @return Entité JPA
     */
    public static UserJpaEntity toEntity(UserAggregate aggregate) {
        if (aggregate == null) {
            return null;
        }

        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(java.util.UUID.randomUUID());
        entity.setMatricule(aggregate.getMatricule().value());
        entity.setFirstname(aggregate.getProfile().getFirstname());
        entity.setLastname(aggregate.getProfile().getLastname());
        entity.setPhone(aggregate.getProfile().getPhone().getNumber());
        entity.setPassword(aggregate.getAuth().getPassword().value());
        entity.setRole(aggregate.getRole().name());
        entity.setStatus("ACTIVE");

        return entity;
    }
}
