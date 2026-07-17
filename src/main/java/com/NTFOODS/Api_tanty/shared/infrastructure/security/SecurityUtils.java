package com.NTFOODS.Api_tanty.shared.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;
import java.util.stream.Collectors;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static String getCurrentMatricule() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "SYSTEM";
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return auth.getName();
    }

    /** Rôles Spring Security de l'utilisateur courant (ex: "ROLE_STOCK", "ROLE_ADMIN"). */
    public static Set<String> getCurrentRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return Set.of();
        return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    /** ROLE_ADMIN et ROLE_DIRECTION (Directeur Général) outrepassent toujours les contrôles de rôle métier. */
    public static boolean hasRoleOrOverride(String requiredRole) {
        Set<String> roles = getCurrentRoles();
        return roles.contains(requiredRole) || roles.contains("ROLE_ADMIN") || roles.contains("ROLE_DIRECTION");
    }
}
