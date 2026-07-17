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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final UserDetailsService userDetailsService;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.userDetailsService = userDetailsService;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable());
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

    // Security headers — Spring Security enables these by default;
    // we just need to ensure frameOptions is DENY (not SAMEORIGIN)
    http.headers(headers -> headers
      .frameOptions(frame -> frame.deny())
    );

    http.authorizeHttpRequests(auth -> auth
      // Endpoints publics
      .requestMatchers("/api/v1/auth/**").permitAll()
      .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
      // Actuator: restrict to localhost in production (health endpoint stays public)
      .requestMatchers("/actuator/health").permitAll()
      .requestMatchers("/actuator/**").hasAuthority(UserRole.ROLE_ADMIN.name())

      // Module Stock - Gestionnaire, Validateur, Direction, Admin, + Production et Finance
      // (nécessaires pour les workflows de réception croisés : un Responsable production
      // valide les réceptions de produits finis, un Comptable valide les réceptions de
      // matière première - cf. cahier des charges §3). Sans ces rôles ici, ces utilisateurs
      // recevaient un 403 au niveau du filtre HTTP avant même d'atteindre le contrôleur,
      // quel que soit le contrôle @PreAuthorize plus fin appliqué ensuite.
      .requestMatchers("/api/v1/stock/**").hasAnyAuthority(
        UserRole.ROLE_STOCK.name(),
        UserRole.ROLE_MAGASINIER.name(),
        UserRole.ROLE_VALIDATEUR.name(),
        UserRole.ROLE_PRODUCTION.name(),
        UserRole.ROLE_FINANCE.name(),
        UserRole.ROLE_DIRECTION.name(),
        UserRole.ROLE_ADMIN.name())

      // Module Stock infrastructure/web controllers - memes roles
      .requestMatchers("/api/stock/**").hasAnyAuthority(
        UserRole.ROLE_STOCK.name(),
        UserRole.ROLE_MAGASINIER.name(),
        UserRole.ROLE_VALIDATEUR.name(),
        UserRole.ROLE_PRODUCTION.name(),
        UserRole.ROLE_FINANCE.name(),
        UserRole.ROLE_DIRECTION.name(),
        UserRole.ROLE_ADMIN.name())

      // Module Commercial
      .requestMatchers("/api/v1/commercial/**").hasAnyAuthority(
        UserRole.ROLE_COMMERCIAL.name(),
        UserRole.ROLE_ADMIN.name())

      // Module Commercial (nouveaux contrôleurs)
      .requestMatchers("/api/commercial/**").hasAnyAuthority(
        UserRole.ROLE_COMMERCIAL.name(),
        UserRole.ROLE_FINANCE.name(),
        UserRole.ROLE_RH.name(),
        UserRole.ROLE_CAISSIER.name(),
        UserRole.ROLE_DIRECTION.name(),
        UserRole.ROLE_ADMIN.name())

      // Module Production
      .requestMatchers("/api/v1/production/**").hasAnyAuthority(
        UserRole.ROLE_PRODUCTION.name(),
        UserRole.ROLE_ADMIN.name())

      // Module Production (nouveaux contrôleurs)
      .requestMatchers("/api/production/**").hasAnyAuthority(
        UserRole.ROLE_PRODUCTION.name(),
        UserRole.ROLE_DIRECTION.name(),
        UserRole.ROLE_ADMIN.name())

      // Module Finance
      .requestMatchers("/api/v1/finance/**").hasAnyAuthority(
        UserRole.ROLE_FINANCE.name(),
        UserRole.ROLE_ADMIN.name())

      // Module Comptabilité (mappé sur ROLE_FINANCE + ROLE_DIRECTION + ROLE_VALIDATEUR)
      .requestMatchers("/api/comptabilite/**").hasAnyAuthority(
        UserRole.ROLE_FINANCE.name(),
        UserRole.ROLE_RH.name(),
        UserRole.ROLE_CAISSIER.name(),
        UserRole.ROLE_COMMERCIAL.name(),
        UserRole.ROLE_VALIDATEUR.name(),
        UserRole.ROLE_DIRECTION.name(),
        UserRole.ROLE_ADMIN.name())

      // Module DG
      .requestMatchers("/api/dg/**").hasAnyAuthority(
        UserRole.ROLE_DIRECTION.name(),
        UserRole.ROLE_ADMIN.name())

      // Module Contrôle
      .requestMatchers("/api/controle/**").hasAnyAuthority(
        UserRole.ROLE_VALIDATEUR.name(),
        UserRole.ROLE_DIRECTION.name(),
        UserRole.ROLE_ADMIN.name())

      // Module RH
      .requestMatchers("/api/v1/rh/**").hasAnyAuthority(
        UserRole.ROLE_RH.name(),
        UserRole.ROLE_ADMIN.name())

      // Direction - lecture seule
      .requestMatchers("/api/v1/direction/**").hasAnyAuthority(
        UserRole.ROLE_DIRECTION.name(),
        UserRole.ROLE_ADMIN.name())

      // Toutes les autres requetes necessitent une authentification
      .anyRequest().authenticated()
    );

    http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowCredentials(true);
    configuration.setAllowedOrigins(Arrays.asList(
      "http://localhost:4200",
      "http://localhost:3000",
      "http://localhost:56621",
      "https://tantyweb.vercel.app/"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
