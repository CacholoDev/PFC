package com.pfcdaw.pfcdaw.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pfcdaw.pfcdaw.model.ClienteEntity;

import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration.time}")
    private Long jwtExpirationMs;

    public String generateToken(ClienteEntity cliente) {
        // Implementa la lógica para generar el token JWT usando jwtSecret y jwtExpirationMs 
        // Retorna el token generado
        return Jwts.builder()
                .setSubject(cliente.getEmail())
                .claim("role", cliente.getRole())
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date((new java.util.Date()).getTime() + jwtExpirationMs))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS512, jwtSecret)
                .compact(); 
    }

}
