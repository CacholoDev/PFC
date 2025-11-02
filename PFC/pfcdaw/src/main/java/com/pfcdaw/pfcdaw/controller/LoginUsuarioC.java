package com.pfcdaw.pfcdaw.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfcdaw.pfcdaw.dto.LoginDto;
import com.pfcdaw.pfcdaw.repository.LoginRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/login")
public class LoginUsuarioC {

    private static final Logger log = LoggerFactory.getLogger(LoginUsuarioC.class);
    private final LoginRepository loginRepository;

    public LoginUsuarioC(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        log.info("Intentando iniciar sesión para el usuario: {}", loginDto.getEmail());
        // Lógica de autenticación aquí
    }

}

/*
     @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDto request) {
        log.info("[POST /auth/login] Intento de login: {}", request.getEmail());
        
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> {
                log.warn("[POST /auth/login] Usuario no encontrado: {}", request.getEmail());
                return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
            });
        
        if (!usuario.getPassword().equals(request.getPassword())) {
            log.warn("[POST /auth/login] Password incorrecta para: {}", request.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }
        
        log.info("[POST /auth/login] Login exitoso: {} (rol: {})", usuario.getEmail(), usuario.getRol());
        
        // Retornar datos do user (o frontend  gardaraos no localStorage)
        return ResponseEntity.ok(Map.of(
            "id", usuario.getId(),
            "email", usuario.getEmail(),
            "rol", usuario.getRol()
        ));
    }
 */