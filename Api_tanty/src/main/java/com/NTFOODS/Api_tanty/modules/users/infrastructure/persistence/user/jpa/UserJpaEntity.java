package com.NTFOODS.Api_tanty.modules.users.infrastructure.persistence.user.jpa;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserJpaEntity - Entité JPA pour la table users
 * Représente un utilisateur dans la base de données
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserJpaEntity {

    @Id
    private UUID id;

    private String matricule;

    private String firstname;

    private String lastname;

    private String phone;

    private String password;

    private String role;

    private String status;
}
