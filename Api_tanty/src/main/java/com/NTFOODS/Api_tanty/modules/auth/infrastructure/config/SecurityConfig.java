package com.NTFOODS.Api_tanty.modules.auth.infrastructure.config;

import com.NTFOODS.Api_tanty.modules.users.domain.enums.UserRole;
import com.NTFOODS.Api_tanty.modules.auth.infrastructure.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * SecurityConfig - Configuration de Spring Security pour l'ERP TANTY
 * Configure l'authentification JWT, les autorisations par rôle et CORS
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Filtre JWT pour l'authentification
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Service de détails utilisateur personnalisé
    private final UserDetailsService userDetailsService;

    /**
     * Constructeur avec injection du filtre JWT et du UserDetailsService
     * @param jwtAuthenticationFilter Filtre d'authentification JWT
     * @param userDetailsService Service de détails utilisateur
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configure le filtre de chaîne de sécurité HTTP
     * Définit les règles d'accès aux endpoints et la stratégie de session
     * @param http Configuration HTTP de Spring Security
     * @return Chaîne de filtres de sécurité
     * @throws Exception En cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Désactiver CSRF car nous utilisons JWT (stateless)
        http.csrf(csrf -> csrf.disable());

        // Configurer CORS pour autoriser les requêtes du frontend
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // Configurer les règles d'autorisation par endpoint
        http.authorizeHttpRequests(auth -> auth
                // Endpoints publics d'authentification (pas de token requis)
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/**").permitAll()

                // Endpoints du module Stock (nécessitent le rôle ROLE_STOCK ou ROLE_ADMIN)
                .requestMatchers("/api/v1/stock/**").hasAnyAuthority(UserRole.ROLE_STOCK.name(), UserRole.ROLE_ADMIN.name())

                // Endpoints du module Commercial (nécessitent le rôle ROLE_COMMERCIAL ou ROLE_ADMIN)
                .requestMatchers("/api/v1/commercial/**").hasAnyAuthority(UserRole.ROLE_COMMERCIAL.name(), UserRole.ROLE_ADMIN.name())

                // Endpoints du module Production (nécessitent le rôle ROLE_PRODUCTION ou ROLE_ADMIN)
                .requestMatchers("/api/v1/production/**").hasAnyAuthority(UserRole.ROLE_PRODUCTION.name(), UserRole.ROLE_ADMIN.name())

                // Endpoints du module Finance (nécessitent le rôle ROLE_FINANCE ou ROLE_ADMIN)
                .requestMatchers("/api/v1/finance/**").hasAnyAuthority(UserRole.ROLE_FINANCE.name(), UserRole.ROLE_ADMIN.name())

                // Endpoints du module RH (nécessitent le rôle ROLE_RH ou ROLE_ADMIN)
                .requestMatchers("/api/v1/rh/**").hasAnyAuthority(UserRole.ROLE_RH.name(), UserRole.ROLE_ADMIN.name())

                // Endpoints de la Direction (lecture seule pour ROLE_DIRECTION)
                .requestMatchers("/api/v1/direction/**").hasAnyAuthority(UserRole.ROLE_DIRECTION.name(), UserRole.ROLE_ADMIN.name())

                // Toutes les autres requêtes nécessitent une authentification
                .anyRequest().authenticated()
        );

        // Configurer la stratégie de session comme STATELESS (pas de session HTTP)
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Ajouter le filtre JWT avant le filtre d'authentification par username/password
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Retourner la chaîne de filtres configurée
        return http.build();
    }

    /**
     * Configure la source de configuration CORS
     * Autorise les requêtes depuis le frontend Angular
     * @return Source de configuration CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Créer une nouvelle configuration CORS
        CorsConfiguration configuration = new CorsConfiguration();

        // Autoriser les credentials (cookies, en-têtes d'autorisation)
        configuration.setAllowCredentials(true);

        // Autoriser les origines spécifiques (frontend Angular)
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:3000","http://localhost:56621"));

        // Autoriser tous les en-têtes
        configuration.setAllowedHeaders(List.of("*"));

        // Autoriser les méthodes HTTP spécifiques
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Exposer les en-têtes personnalisés
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Créer une source de configuration basée sur URL
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Appliquer la configuration à tous les chemins
        source.registerCorsConfiguration("/**", configuration);

        // Retourner la source de configuration
        return source;
    }

    /**
     * Bean pour le gestionnaire d'authentification
     * Utilisé pour authentifier les utilisateurs avec username/password
     * @param config Configuration d'authentification Spring
     * @return Gestionnaire d'authentification
     * @throws Exception En cas d'erreur de configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // Retourner le gestionnaire d'authentification configuré
        return config.getAuthenticationManager();
    }

    /**
     * Bean pour le fournisseur d'authentification
     * Configure le UserDetailsService et le PasswordEncoder
     * @param passwordEncoder Encodeur de mot de passe
     * @return Fournisseur d'authentification
     */
    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * Bean pour l'encodeur de mot de passe
     * Utilise BCrypt pour le hachage sécurisé des mots de passe
     * @return Encodeur de mot de passe BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Retourner un nouvel encodeur BCrypt
        return new BCryptPasswordEncoder();
    }
}
