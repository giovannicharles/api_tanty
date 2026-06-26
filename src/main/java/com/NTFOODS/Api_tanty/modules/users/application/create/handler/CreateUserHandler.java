package com.NTFOODS.Api_tanty.modules.users.application.create.handler;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.NTFOODS.Api_tanty.modules.users.application.create.command.CreateUserCommand;
import com.NTFOODS.Api_tanty.modules.users.domain.aggregate.UserAggregate;
import com.NTFOODS.Api_tanty.modules.users.domain.entity.UserAuth;
import com.NTFOODS.Api_tanty.modules.users.domain.entity.UserProfile;
import com.NTFOODS.Api_tanty.modules.users.domain.repository.UserRepository;
import com.NTFOODS.Api_tanty.modules.users.domain.service.MatriculeGenerator;
import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.EmailAddress;
import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.PasswordHash;
import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.PhoneNumber;
import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.UserMatricule;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import lombok.*;

/**
 * CreateUserHandler - Handler pour la création d'utilisateurs
 * Gère la logique métier pour créer un nouvel utilisateur dans le système
 */
@Component
public class CreateUserHandler {

    // Repository pour accéder aux données des utilisateurs
    private final UserRepository repo;

    // Générateur de matricules pour créer des identifiants uniques
    private final MatriculeGenerator generator;

    // Encodeur de mot de passe pour sécuriser les mots de passe
    private final PasswordEncoder encoder;

  public CreateUserHandler(UserRepository repo, MatriculeGenerator generator, PasswordEncoder encoder) {
    this.repo = repo;
    this.generator = generator;
    this.encoder = encoder;
  }

  /**
     * Gère la commande de création d'utilisateur
     * Crée un nouvel utilisateur avec les informations fournies
     * @param cmd Commande de création d'utilisateur
     */
    public void handle(CreateUserCommand cmd) {

        // Générer un matricule unique pour l'utilisateur
        UserMatricule matricule = generator.generate(
            repo.count() + 1,
            "USR"
        );

        // Créer le profil utilisateur avec toutes les informations
        UserProfile profile = new UserProfile(
            cmd.firstname(),
            cmd.lastname(),
            new PhoneNumber(cmd.phone()),
            cmd.address(),
            cmd.cni(),
            cmd.dateOfBirth(),
            cmd.placeOfBirth(),
            cmd.nationality(),
            cmd.level(),
            cmd.maritalStatus()
        );

        // Créer les informations d'authentification avec le mot de passe haché et email
        UserAuth auth = new UserAuth(
            new PasswordHash(
                encoder.encode(cmd.password())
            )
        );

        // Définir l'email si fourni
        if (cmd.email() != null && !cmd.email().isEmpty()) {
            auth.setEmail(new EmailAddress(cmd.email()));
        }

        // Créer l'agrégat utilisateur avec le matricule comme identifiant
        // UserId utilise le matricule (String) et non un UUID
        UserAggregate user = new UserAggregate(
            new UserId(matricule.value()),
            matricule,
            profile,
            auth,
            cmd.role()
        );

        // Sauvegarder l'utilisateur dans le repository
        repo.save(user);
    }
}
