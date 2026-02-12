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
import com.pfcdaw.pfcdaw.exception.BusinessException;
import com.pfcdaw.pfcdaw.model.ClienteEntity;
import com.pfcdaw.pfcdaw.repository.ClienteRepository;
import com.pfcdaw.pfcdaw.security.JwtTokenProvider;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class LoginUsuarioC {

    private static final Logger log = LoggerFactory.getLogger(LoginUsuarioC.class);
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginUsuarioC(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginDto loginDto) {
        // Buscar cliente por email
        log.info("[POST /auth/login] Intentando login para: {}", loginDto.getEmail());
        ClienteEntity cliente = clienteRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> {
                    log.warn("[POST /auth/login] Cliente no encontrado: {}", loginDto.getEmail());
                    return new BusinessException("Correo inválido");
                });
        
        // Verificar contraseña con BCrypt (comparando texto plano vs hash)
        if (!passwordEncoder.matches(loginDto.getPassword(), cliente.getPassword())) {
            log.warn("[POST /auth/login] Contraseña incorrecta para el cliente: {}", loginDto.getEmail());
            throw new BusinessException("Contraseña inválida");
        }
        
        // Login exitoso - GENERAR TOKEN JWT
        log.info("[POST /auth/login] Login exitoso: {} (rol: {})", cliente.getEmail(), cliente.getRole());
        
        // Genera el JWT token usando el JwtTokenProvider
        String token = jwtTokenProvider.generateToken(cliente);
        log.debug("[POST /auth/login] Token JWT generado para: {}", cliente.getEmail());
        
        // Devolver datos del cliente + el token JWT
        return ResponseEntity.ok(Map.of(
                "token", token,  // ⭐ El token JWT para incluir en futuros requests
                "id", cliente.getId(),
                "nombre", cliente.getNombre(),
                "apellido", cliente.getApellido(),
                "email", cliente.getEmail(),
                "rol", cliente.getRole()));
    }

}
