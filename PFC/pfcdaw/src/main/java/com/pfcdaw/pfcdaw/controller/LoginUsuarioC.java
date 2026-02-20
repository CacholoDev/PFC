package com.pfcdaw.pfcdaw.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfcdaw.pfcdaw.dto.LoginDto;
import com.pfcdaw.pfcdaw.dto.RefreshTokenRequestDto;
import com.pfcdaw.pfcdaw.exception.BusinessException;
import com.pfcdaw.pfcdaw.model.ClienteEntity;
import com.pfcdaw.pfcdaw.model.RefreshTokenEntity;
import com.pfcdaw.pfcdaw.repository.ClienteRepository;
import com.pfcdaw.pfcdaw.security.JwtTokenProvider;
import com.pfcdaw.pfcdaw.service.LimitesIntentosService;
import com.pfcdaw.pfcdaw.service.RefreshTokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class LoginUsuarioC {

    private static final Logger log = LoggerFactory.getLogger(LoginUsuarioC.class);
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final LimitesIntentosService limitesIntentosService;
    private final RefreshTokenService refreshTokenService;

    public LoginUsuarioC(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, LimitesIntentosService limitesIntentosService, RefreshTokenService refreshTokenService) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.limitesIntentosService = limitesIntentosService;
        this.refreshTokenService = refreshTokenService;
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginDto loginDto) {
        String email = loginDto.getEmail();

        // Verificar límites de intentos usando Bucket4J
        if (!limitesIntentosService.tryLogin(email)) {
            long remainingTokens = limitesIntentosService.getAvailableTokens(email);
            log.warn("[POST /auth/login] Límite de intentos excedido para: {} (restantes: {})", email, remainingTokens);
            throw new BusinessException("Demasiados intentos fallidos. Intenta nuevamente más tarde.");
        }

        // Buscar cliente por email
        log.info("[POST /auth/login] Intentando login para: {}", email);
        ClienteEntity cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("[POST /auth/login] Cliente no encontrado: {}", email);
                    return new BusinessException("Correo inválido");
                });
        // Verificar contraseña con BCrypt (comparando texto plano vs hash)
        if (!passwordEncoder.matches(loginDto.getPassword(), cliente.getPassword())) {
            log.warn("[POST /auth/login] Contraseña incorrecta para el cliente: {}", email);
            throw new BusinessException("Contraseña inválida");
        }
        
        // Login exitoso - GENERAR TOKEN JWT
        log.info("[POST /auth/login] Login exitoso: {} (rol: {})", cliente.getEmail(), cliente.getRole());
        
        // Genera el access token y refresh token
        String token = jwtTokenProvider.generateToken(cliente);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(cliente);
        log.debug("[POST /auth/login] Token JWT generado para: {}", cliente.getEmail());
        
        // Devolver datos del cliente + el token JWT
        return ResponseEntity.ok(Map.of(
                "token", token,  // ⭐ El token JWT para incluir en futuros requests
                "refreshToken", refreshToken.getToken(),
                "id", cliente.getId(),
                "nombre", cliente.getNombre(),
                "apellido", cliente.getApellido(),
                "email", cliente.getEmail(),
                "rol", cliente.getRole()));
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new BusinessException("Refresh token invalido");
        }

        RefreshTokenEntity currentToken = refreshTokenService.validateRefreshToken(refreshToken);
        RefreshTokenEntity newToken = refreshTokenService.rotateRefreshToken(currentToken);

        String accessToken = jwtTokenProvider.generateToken(currentToken.getCliente());

        return ResponseEntity.ok(Map.of(
                "token", accessToken,
                "refreshToken", newToken.getToken()));
    }

}
