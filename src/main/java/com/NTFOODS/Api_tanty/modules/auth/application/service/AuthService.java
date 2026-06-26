package com.NTFOODS.Api_tanty.modules.auth.application.service;

import com.NTFOODS.Api_tanty.modules.auth.application.dto.AuthResponse;
import com.NTFOODS.Api_tanty.modules.auth.application.dto.LoginRequest;
import com.NTFOODS.Api_tanty.modules.users.domain.aggregate.UserAggregate;
import com.NTFOODS.Api_tanty.modules.users.domain.repository.UserRepository;
import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.UserMatricule;
import com.NTFOODS.Api_tanty.modules.auth.infrastructure.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * AuthService - Service pour la logique métier d'authentification
 * Gère la connexion des utilisateurs et la génération des tokens JWT
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    // Gestionnaire d'authentification Spring Security
    private final AuthenticationManager authenticationManager;

    // Service JWT pour la génération des tokens
    private final JwtService jwtService;

    // Repository utilisateur pour accéder aux données utilisateur
    private final UserRepository userRepository;

    /**
     * Constructeur avec injection des dépendances
     * @param authenticationManager Gestionnaire d'authentification
     * @param jwtService Service JWT
     * @param userRepository Repository utilisateur
     */
    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    /**
     * Authentifie un utilisateur et génère un token JWT
     * @param loginRequest Données de connexion (matricule et mot de passe)
     * @return Réponse d'authentification contenant le token JWT
     */
    public AuthResponse login(LoginRequest loginRequest) {
        log.info("Tentative de connexion pour le matricule: {}", loginRequest.getMatricule());
        
        // Créer un token d'authentification avec les identifiants fournis
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            loginRequest.getMatricule(),
            loginRequest.getPassword()
        );

        try {
            // Authentifier l'utilisateur via Spring Security
            Authentication authentication = authenticationManager.authenticate(authToken);
            log.info("Authentification réussie pour le matricule: {}", loginRequest.getMatricule());

            // Récupérer les détails de l'utilisateur authentifié
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Générer un token JWT pour l'utilisateur
            String token = jwtService.generateToken(userDetails);

            // Récupérer l'agrégat utilisateur depuis la base de données
            UserAggregate userAggregate = userRepository.findFirstByMatricule(new UserMatricule(loginRequest.getMatricule()))
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Construire et retourner la réponse d'authentification
            return new AuthResponse(
                token,
                userAggregate.getMatricule().value(),
                userAggregate.getProfile().getFirstname(),
                userAggregate.getProfile().getLastname(),
                userAggregate.getRole().name()
            );
        } catch (BadCredentialsException e) {
            log.error("Identifiants incorrects pour le matricule: {}", loginRequest.getMatricule());
            throw new RuntimeException("Identifiants incorrects", e);
        } catch (DisabledException e) {
            log.error("Compte désactivé pour le matricule: {}", loginRequest.getMatricule());
            throw new RuntimeException("Compte désactivé", e);
        } catch (LockedException e) {
            log.error("Compte verrouillé pour le matricule: {}", loginRequest.getMatricule());
            throw new RuntimeException("Compte verrouillé", e);
        } catch (AuthenticationException e) {
            log.error("Erreur d'authentification pour le matricule: {} - {}", loginRequest.getMatricule(), e.getMessage());
            throw new RuntimeException("Erreur d'authentification", e);
        }
    }

    /**
     * Déconnecte un utilisateur
     * Note: Avec JWT stateless, la déconnexion est gérée côté client
     * en supprimant le token. Cette méthode peut être utilisée pour
     * enregistrer la déconnexion dans les logs ou invalider le token
     * si un système de blacklist est implémenté.
     * @param matricule Matricule de l'utilisateur à déconnecter
     */
    public void logout(String matricule) {
        // TODO: Enregistrer la déconnexion dans les logs
        // TODO: Si un système de blacklist de tokens est implémenté, ajouter le token ici

        // Pour l'instant, la déconnexion est gérée côté client
        // en supprimant le token du localStorage
    }
}
