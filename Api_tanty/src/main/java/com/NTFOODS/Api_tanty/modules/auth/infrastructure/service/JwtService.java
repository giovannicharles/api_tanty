package com.NTFOODS.Api_tanty.modules.auth.infrastructure.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtService - Service pour la gestion des tokens JWT
 * Génère, valide et extrait les informations des tokens JWT
 */
@Service
public class JwtService {

    // Clé secrète pour signer les tokens (en production, utiliser une variable d'environnement)
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    // Durée de validité du token d'accès (24 heures)
    private static final long JWT_EXPIRATION = 86400000; // 24 heures en millisecondes

    /**
     * Extrait le matricule utilisateur du token JWT
     * @param token Token JWT
     * @return Matricule de l'utilisateur
     */
    public String extractMatricule(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait une revendication spécifique du token JWT
     * @param token Token JWT
     * @param claimsResolver Fonction pour extraire la revendication
     * @return Valeur de la revendication
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Génère un token JWT pour un utilisateur
     * @param userDetails Détails de l'utilisateur
     * @return Token JWT généré
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Génère un token JWT avec des revendications supplémentaires
     * @param extraClaims Revendications supplémentaires
     * @param userDetails Détails de l'utilisateur
     * @return Token JWT généré
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Vérifie si le token est valide pour l'utilisateur donné
     * @param token Token JWT
     * @param userDetails Détails de l'utilisateur
     * @return true si le token est valide, false sinon
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String matricule = extractMatricule(token);
        return (matricule.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Vérifie si le token est expiré
     * @param token Token JWT
     * @return true si le token est expiré, false sinon
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrait la date d'expiration du token
     * @param token Token JWT
     * @return Date d'expiration
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait toutes les revendications du token
     * @param token Token JWT
     * @return Revendications du token
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Obtient la clé de signature pour les tokens
     * @return Clé de signature
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
