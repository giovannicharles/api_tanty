package com.NTFOODS.Api_tanty.modules.auth.infrastructure.service;

import com.NTFOODS.Api_tanty.modules.users.domain.aggregate.UserAggregate;
import com.NTFOODS.Api_tanty.modules.users.domain.repository.UserRepository;
import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.UserMatricule;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * CustomUserDetailsService - Service personnalisé de détails utilisateur pour Spring Security
 * Charge les informations utilisateur depuis la base de données pour l'authentification
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Repository utilisateur pour accéder aux données utilisateur
    private final UserRepository userRepository;

    /**
     * Constructeur avec injection du repository utilisateur
     * @param userRepository Repository utilisateur
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Charge les détails utilisateur par matricule
     * @param matricule Matricule de l'utilisateur
     * @return Détails de l'utilisateur pour Spring Security
     * @throws UsernameNotFoundException Si l'utilisateur n'est pas trouvé
     */
    @Override
    public UserDetails loadUserByUsername(String matricule) throws UsernameNotFoundException {
        // Rechercher l'utilisateur par matricule dans la base de données
        UserAggregate userAggregate = userRepository.findByMatricule(new UserMatricule(matricule))
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec le matricule: " + matricule));

        // Vérifier si l'utilisateur est actif
        if (userAggregate.getStatus() != com.NTFOODS.Api_tanty.modules.users.domain.enums.UserStatus.ACTIVE) {
            throw new UsernameNotFoundException("Utilisateur non actif: " + matricule);
        }

        // Vérifier si le compte est verrouillé
        if (userAggregate.getAuth().isLocked()) {
            throw new UsernameNotFoundException("Compte verrouillé: " + matricule);
        }

        // Créer les autorités basées sur le rôle de l'utilisateur.
        // BUG CORRIGÉ : userAggregate.getRole().name() vaut déjà "ROLE_STOCK", "ROLE_FINANCE", etc.
        // (voir l'enum UserRole) ; préfixer à nouveau par "ROLE_" produisait "ROLE_ROLE_STOCK",
        // ce qui empêchait tout contrôle @PreAuthorize("hasRole('STOCK')") ou hasAuthority('ROLE_STOCK')
        // de fonctionner pour qui que ce soit.
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userAggregate.getRole().name());

        // Construire et retourner l'objet UserDetails pour Spring Security
        return User.builder()
                .username(userAggregate.getMatricule().value()) // Utiliser le matricule comme username
                .password(userAggregate.getAuth().getPassword().value()) // Mot de passe haché
                .authorities(Collections.singletonList(authority)) // Autorités basées sur le rôle
                .accountLocked(userAggregate.getAuth().isLocked()) // Statut de verrouillage
                .credentialsExpired(false) // Les identifiants ne sont pas expirés
                .accountExpired(false) // Le compte n'est pas expiré
                .disabled(userAggregate.getStatus() != com.NTFOODS.Api_tanty.modules.users.domain.enums.UserStatus.ACTIVE) // Compte désactivé si non actif
                .build();
    }
}
