package com.pfcdaw.pfcdaw.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pfcdaw.pfcdaw.dto.LoginDto;
import com.pfcdaw.pfcdaw.model.ClienteEntity;
import com.pfcdaw.pfcdaw.repository.ClienteRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class LoginUsuarioC {

    private static final Logger log = LoggerFactory.getLogger(LoginUsuarioC.class);
    private final ClienteRepository clienteRepository;

    public LoginUsuarioC(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginDto loginDto) {
        // Buscar el cliente por email
        log.info("[POST /auth/login] Intentando login para: {}", loginDto.getEmail());
        ClienteEntity cliente = clienteRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> {
                    log.warn("[POST /auth/login] Cliente no encontrado: {}", loginDto.getEmail());
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo inválido");
                });
        // Verificar la contraseña
        if (!cliente.getPassword().equals(loginDto.getPassword())) {
            log.warn("[POST /auth/login] Contraseña incorrecta para el cliente: {}", loginDto.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Contraseña inválida");
        }
        // Login exitoso - devolver solo datos necesarios
        log.info("[POST /auth/login] Login exitoso: {} (rol: {})", cliente.getEmail(), cliente.getRole());
        return ResponseEntity.ok(Map.of(
                "id", cliente.getId(),
                "nombre", cliente.getNombre(),
                "apellido", cliente.getApellido(),
                "email", cliente.getEmail(),
                "rol", cliente.getRole()));
    }

}
