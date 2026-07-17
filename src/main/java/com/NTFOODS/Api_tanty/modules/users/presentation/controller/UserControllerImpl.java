package com.NTFOODS.Api_tanty.modules.users.presentation.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.NTFOODS.Api_tanty.modules.users.application.create.command.CreateUserCommand;
import com.NTFOODS.Api_tanty.modules.users.application.create.handler.CreateUserHandler;
import com.NTFOODS.Api_tanty.modules.users.infrastructure.persistence.user.jpa.UserJpaEntity;
import com.NTFOODS.Api_tanty.modules.users.infrastructure.persistence.user.jpa.UserJpaRepository;

@RestController
public class UserControllerImpl implements UserControllerApi {
    private final CreateUserHandler create;
    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public UserControllerImpl(CreateUserHandler create, UserJpaRepository userJpaRepository, PasswordEncoder passwordEncoder) {
        this.create = create;
        this.userJpaRepository = userJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void create(@RequestBody CreateUserCommand cmd) {
        create.handle(cmd);
    }

    @PutMapping("/api/v1/users/{matricule}/profile")
    public ResponseEntity<Map<String, String>> updateProfile(
            @PathVariable String matricule,
            @RequestBody Map<String, String> body) {
        List<UserJpaEntity> users = userJpaRepository.findAllByMatricule(matricule);
        if (users.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Utilisateur non trouvé"));
        }
        UserJpaEntity user = users.get(0);
        if (body.containsKey("firstname")) user.setFirstname(body.get("firstname"));
        if (body.containsKey("lastname")) user.setLastname(body.get("lastname"));
        if (body.containsKey("phone")) user.setPhone(body.get("phone"));
        userJpaRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Profil mis à jour avec succès"));
    }

    @PutMapping("/api/v1/users/{matricule}/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @PathVariable String matricule,
            @RequestBody Map<String, String> body) {
        List<UserJpaEntity> users = userJpaRepository.findAllByMatricule(matricule);
        if (users.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Utilisateur non trouvé"));
        }
        UserJpaEntity user = users.get(0);
        String currentPassword = body.getOrDefault("currentPassword", "");
        String newPassword = body.getOrDefault("newPassword", "");

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mot de passe actuel incorrect"));
        }
        if (newPassword.length() < 8) {
            return ResponseEntity.badRequest().body(Map.of("message", "Le nouveau mot de passe doit faire au moins 8 caractères"));
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userJpaRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Mot de passe modifié avec succès"));
    }
}
