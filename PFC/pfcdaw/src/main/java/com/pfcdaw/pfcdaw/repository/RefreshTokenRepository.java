package com.pfcdaw.pfcdaw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pfcdaw.pfcdaw.model.RefreshTokenEntity;

/**
 * Repositorio para gestionar Refresh Tokens en BD
 * 
 * Métodos automáticos que Spring JPA genera:
 * - save(entity) → guarda un refresh token
 * - findById(id) → busca por ID
 * - delete(entity) → elimina un token
 * - findAll() → obtiene todos
 * 
 * Métodos custom que definimos abajo:
 * - findByToken() → busca un token específico (para validar en /auth/refresh)
 * - findByClienteIdAndRevokedFalse() → tokens válidos de un cliente
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    /**
     * Busca un refresh token por su cadena de texto
     * Útil en POST /auth/refresh para validar que el token existe
     * 
     * @param token - El refresh token que envía el cliente
     * @return Optional con el token si existe, vacío si no
     */
    Optional<RefreshTokenEntity> findByToken(String token);

    /**
     * Obtiene todos los refresh tokens NO revocados de un cliente
     * Práctica: cuando usuario hace logout, revocar todos excepto este
     * 
     * @param clienteId - ID del cliente
     * @return Lista de tokens válidos (no revocados)
     */
    List<RefreshTokenEntity> findByClienteIdAndRevokedFalse(Long clienteId);

    /**
     * Busca refresh tokens por cliente
     * Usado cuando queremos ver/revocar todos los tokens de un usuario
     * 
     * @param clienteId - ID del cliente
     * @return Lista de todos los tokens (revocados y válidos)
     */
    List<RefreshTokenEntity> findByClienteId(Long clienteId);

    /**
     * Query custom: revocar TODOS los tokens de un cliente
     * Usado cuando usuario cambia contraseña = logout en todos los dispositivos
     * 
     * @param clienteId - ID del cliente
     */
    @Query("UPDATE RefreshTokenEntity rt SET rt.revoked = true WHERE rt.cliente.id = :clienteId")
    void revokeAllTokensByClienteId(@Param("clienteId") Long clienteId);
}
