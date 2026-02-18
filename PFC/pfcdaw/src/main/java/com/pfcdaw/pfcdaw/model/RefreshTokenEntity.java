package com.pfcdaw.pfcdaw.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entidad para almacenar Refresh Tokens
 * 
 * El refresh token permite obtener un nuevo access token sin volver a meter credenciales
 * - Access Token: corta vida (15 min) = menos riesgo si se roba
 * - Refresh Token: larga vida (7 días) = guardado en BD, se puede revocar
 * 
 * Flujo:
 * 1. Login → recibe access_token + refresh_token
 * 2. Cada 15min → access_token expira
 * 3. Frontend usa refresh_token en POST /auth/refresh
 * 4. Backend devuelve nuevo access_token (sin pedir credenciales)
 * 5. Si usuario cambia contraseña → invalida refresh_tokens → logout automático
 */
@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Relación ManyToOne: 
     * - Muchos refresh tokens pueden pertenecer a 1 cliente
     * - Si eliminas un cliente, automaticamente se eliminan sus refresh tokens (cascade)
     * - No se devuelve al convertir a JSON (@JsonIgnore)
     */
    @ManyToOne(optional = false)
    @JoinColumn(
        name = "cliente_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_refresh_tokens_cliente")
    )
    @JsonIgnore // No devuelves el cliente completo en JSON, solo el token
    @ToString.Exclude // Evita LazyInitializationException
    private ClienteEntity cliente;

    /**
     * El token JWT de refresh
     * Ejemplo:
     * eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGVtYWlsLmNvbSIsImNsaWVudGVJZCI6MSwiaWF0IjoxNzcxNDY4OTExLCJleHAiOjE3NzI5MTk0ODF9...
     */
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String token;

    /**
     * Fecha de expiración del refresh token
     * Ejemplo: 2026-02-25 (7 días desde la creación)
     */
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    /**
     * Fecha de creación (automática)
     * Hibernate lo rellena con LocalDateTime.now() al hacer .save()
     */
    @CreationTimestamp
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * ¿Está revocado?
     * true = invalido (usuario cambió contraseña, logout, etc.)
     * false = válido
     * 
     * Por qué un boolean en lugar de deletearlo:
     * - Auditoria: puedes ver cuándo y cuál token fue revocado
     * - Performance: más rápido marcar como revocado que eliminar
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean revoked = false;


}
