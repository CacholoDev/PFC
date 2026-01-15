package com.pfcdaw.pfcdaw.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pfcdaw.pfcdaw.model.ClienteEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Proveedor de tokens JWT
 * Genera, valida y extrae información de tokens JWT
 */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private Long jwtExpirationMs;

    /**
     * Genera un token JWT para un cliente
     * @param cliente - El cliente para el cual generar el token
     * @return String - El token JWT generado
     */
    public String generateToken(ClienteEntity cliente) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        log.debug("Generando token JWT para: {}", cliente.getEmail());
        
        return Jwts.builder()
                .subject(cliente.getEmail())
                .claim("role", cliente.getRole().toString())
                .claim("id", cliente.getId())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Valida un token JWT (firma y expiración)
     * @param token - El token a validar
     * @return boolean - true si es válido, false si no
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            
            // API nueva de jjwt 0.12+
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            
            log.debug("Token JWT válido");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error validando token JWT: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extrae el email (subject) del token
     * @param token - El token JWT
     * @return String - El email del usuario
     */
    public String getEmailFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return claims.getSubject();
    }

    /**
     * Extrae el rol del token
     * @param token - El token JWT
     * @return String - El rol del usuario (ADMIN, USER)
     */
    public String getRoleFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return claims.get("role", String.class);
    }

    /**
     * Extrae el ID del usuario del token
     * @param token - El token JWT
     * @return Long - El ID del usuario
     */
    public Long getIdFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return claims.get("id", Long.class);
    }

}
