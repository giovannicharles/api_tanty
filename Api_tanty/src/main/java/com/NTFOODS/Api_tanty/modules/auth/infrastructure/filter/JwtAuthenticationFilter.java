package com.NTFOODS.Api_tanty.modules.auth.infrastructure.filter;

import com.NTFOODS.Api_tanty.modules.users.domain.aggregate.UserAggregate;
import com.NTFOODS.Api_tanty.modules.users.domain.repository.UserRepository;
import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.UserMatricule;
import com.NTFOODS.Api_tanty.modules.auth.infrastructure.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthenticationFilter - Filtre d'authentification JWT
 * Intercepte chaque requête pour vérifier la présence et la validité du token JWT
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Service JWT pour la validation des tokens
    private final JwtService jwtService;

    // Service de détails utilisateur pour charger les informations de l'utilisateur
    private final UserDetailsService userDetailsService;

    // Repository utilisateur pour récupérer les informations de l'utilisateur
    private final UserRepository userRepository;

    /**
     * Constructeur avec injection des dépendances
     * @param jwtService Service JWT
     * @param userDetailsService Service de détails utilisateur
     * @param userRepository Repository utilisateur
     */
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    /**
     * Méthode principale du filtre
     * Vérifie le token JWT et authentifie l'utilisateur si valide
     * @param request Requête HTTP entrante
     * @param response Réponse HTTP sortante
     * @param filterChain Chaîne de filtres
     * @throws ServletException En cas d'erreur de servlet
     * @throws IOException En cas d'erreur d'entrée/sortie
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Ignorer les endpoints d'authentification publics
        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraire le token JWT de l'en-tête Authorization
        final String authHeader = request.getHeader("Authorization");

        // Vérifier si l'en-tête est null ou ne commence pas par "Bearer "
        final String jwt;
        final String userMatricule;

        // Si l'en-tête est null ou ne commence pas par "Bearer ", passer au filtre suivant
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraire le token (enlever "Bearer ")
        jwt = authHeader.substring(7);

        // Extraire le matricule utilisateur du token
        userMatricule = jwtService.extractMatricule(jwt);

        // Si le matricule est extrait et qu'il n'y a pas déjà une authentification dans le contexte
        if (userMatricule != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Charger les détails de l'utilisateur depuis la base de données
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userMatricule);

            // Vérifier si le token est valide pour cet utilisateur
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Créer un token d'authentification avec les détails de l'utilisateur
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Ajouter les détails de la requête au token d'authentification
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Définir l'authentification dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Mettre à jour la date de dernière connexion de l'utilisateur
                UserAggregate userAggregate = userRepository.findByMatricule(new UserMatricule(userMatricule))
                        .orElse(null);
                if (userAggregate != null && userAggregate.getProfile() != null) {
                    userAggregate.getProfile().updateLastLogin();
                    userRepository.save(userAggregate);
                }
            }
        }

        // Passer au filtre suivant dans la chaîne
        filterChain.doFilter(request, response);
    }
}
