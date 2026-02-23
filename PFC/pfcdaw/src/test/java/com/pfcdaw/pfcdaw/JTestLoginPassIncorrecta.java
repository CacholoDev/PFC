package com.pfcdaw.pfcdaw;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.pfcdaw.pfcdaw.controller.LoginUsuarioC;
import com.pfcdaw.pfcdaw.dto.LoginDto;
import com.pfcdaw.pfcdaw.exception.BusinessException;
import com.pfcdaw.pfcdaw.model.ClienteEntity;
import com.pfcdaw.pfcdaw.model.LoginRoleEnum;
import com.pfcdaw.pfcdaw.repository.ClienteRepository;
import com.pfcdaw.pfcdaw.security.JwtTokenProvider;
import com.pfcdaw.pfcdaw.service.LimitesIntentosService;
import com.pfcdaw.pfcdaw.service.RefreshTokenService;

/**
 * TEST: Login con contraseña incorrecta
 * 
 * Verifica que el sistema rechaza un login cuando:
 * - El email existe en BD
 * - Pero la contraseña es INCORRECTA
 */
@WebMvcTest(LoginUsuarioC.class)
public class JTestLoginPassIncorrecta {

    // ========== SOLO LOS MOCKS NECESARIOS ==========
    @MockitoBean
    private ClienteRepository clienteRepository;
    
    @MockitoBean
    private PasswordEncoder passwordEncoder;
    
    @MockitoBean
    private LimitesIntentosService limitesIntentosService;
    
    @MockitoBean
    private RefreshTokenService refreshTokenService;
    
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    // ========== CONTROLLER A TESTEAR ==========
    @Autowired
    private LoginUsuarioC loginController;

    private ClienteEntity cliente;
    private LoginDto loginDto;

    @BeforeEach
    void setUp() {
        // Crear cliente CON TODOS LOS CAMPOS OBLIGATORIOS
        cliente = ClienteEntity.builder()
            .id(1L)
            .nombre("Admin")
            .apellido("Panaderia")
            .email("admin@panaderia.com")  // ✅ Email real que existe en la BD
            .direccion("Calle Principal 123")
            .nombreEmpresa("Panaderia")
            .telefono("123456789")
            .password("$2a$10$N9qo8uLOickgx2ZMRZoMye")  // Hash BCrypt real (admin123)
            .role(LoginRoleEnum.ADMIN)
            .build();
        
        // DTO con credenciales INCORRECTAS
        loginDto = new LoginDto();
        loginDto.setEmail("admin@panaderia.com");
        loginDto.setPassword("passwordIncorrecta");  // ❌ Password INCORRECTA
    }

    @Test
    void testLoginPasswordIncorrecta() {
        // ARRANGE: Configurar mocks
        when(limitesIntentosService.tryLogin("admin@panaderia.com")).thenReturn(true);
        when(clienteRepository.findByEmail("admin@panaderia.com")).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("passwordIncorrecta", cliente.getPassword())).thenReturn(false);
        
        // ACT + ASSERT
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            loginController.login(loginDto);
        });
        
        assertEquals("Contraseña inválida", exception.getMessage());
    }
}
