package com.pfcdaw.pfcdaw.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfcdaw.pfcdaw.dto.RegisterDTO;
import com.pfcdaw.pfcdaw.exception.BusinessException;
import com.pfcdaw.pfcdaw.model.ClienteEntity;
import com.pfcdaw.pfcdaw.model.LoginRoleEnum;
import com.pfcdaw.pfcdaw.repository.ClienteRepository;
import com.pfcdaw.pfcdaw.security.JwtTokenProvider;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class RegistrerController {
    
    private static final Logger log = LoggerFactory.getLogger(RegistrerController.class);
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    public RegistrerController(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterDTO registerDTO) {
        log.info("[POST /auth/register] Registrando nuevo usuario: {}", registerDTO.getEmail());
        
        // 1. Verificar que el email NO exista
        if (clienteRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            log.warn("[POST /auth/register] Email ya existe: {}", registerDTO.getEmail());
            throw new BusinessException( 
                "El email '" + registerDTO.getEmail() + "' ya está en uso");
        }
        
        // 2. Crear el cliente con rol USER forzado
        ClienteEntity nuevoCliente = ClienteEntity.builder()
            .nombre(registerDTO.getNombre())
            .apellido(registerDTO.getApellido())
            .email(registerDTO.getEmail())
            .telefono(registerDTO.getTelefono())
            .direccion(registerDTO.getDireccion())
            .nombreEmpresa(registerDTO.getNombreEmpresa())
            .password(passwordEncoder.encode(registerDTO.getPassword())) // ✅ Hashear password
            .role(LoginRoleEnum.USER) // ✅ Forzar rol USER
            .build();
        
        // 3. Guardar en BD
        ClienteEntity clienteGuardado = clienteRepository.save(nuevoCliente);
        log.info("[POST /auth/register] Usuario registrado con ID: {}", clienteGuardado.getId());
        
        // 4. Generar token JWT (usuario ya logueado)
        String token = jwtTokenProvider.generateToken(clienteGuardado);
        log.debug("[POST /auth/register] Token JWT generado para: {}", clienteGuardado.getEmail());
        
        // 5. Devolver token + datos (igual que login)
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "token", token,
            "id", clienteGuardado.getId(),
            "nombre", clienteGuardado.getNombre(),
            "apellido", clienteGuardado.getApellido(),
            "email", clienteGuardado.getEmail(),
            "rol", clienteGuardado.getRole()
        ));
    }

}
