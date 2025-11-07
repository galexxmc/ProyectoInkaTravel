package com.inkatravel.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // --- 1. CLAVE SECRETA ---
    // Esta es la "firma" secreta de tu aplicación. ¡Debe ser larga y segura!
    // Generé una para ti (es "InkaTravelSuperSecretKeyForJWTGeneration2025" en Base64).
    // NUNCA expongas esta clave públicamente.
    @Value("${jwt.secret.key}") // Lee la clave desde application.properties
    private String SECRET_KEY;

    // --- 2. TIEMPO DE EXPIRACIÓN (en milisegundos) ---
    // Vamos a hacer que los tokens duren 24 horas.
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 horas

    /**
     * Genera un token JWT para un usuario (UserDetails).
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Genera un token JWT con "claims" (datos extra) opcionales.
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims) // Añade datos extra (ej: roles)
                .setSubject(userDetails.getUsername()) // El "subject" es el correo del usuario
                .setIssuedAt(new Date(System.currentTimeMillis())) // Fecha de creación
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Fecha de expiración
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Firma el token
                .compact(); // Construye el String
    }

    // --- Métodos de Validación y Extracción ---

    /**
     * Verifica si un token es válido (si no ha expirado y si el usuario es correcto).
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token); // Saca el correo del token
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Verifica si el token ha expirado.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración del token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae el "subject" (el correo/username) del token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Método genérico para extraer cualquier "Claim" (dato) del token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // --- Métodos Privados (Helpers) ---

    /**
     * Parsea el token y extrae TODOS sus datos (Claims).
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Obtiene la clave de firma (Key) a partir de nuestro String SECRETO en Base64.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}