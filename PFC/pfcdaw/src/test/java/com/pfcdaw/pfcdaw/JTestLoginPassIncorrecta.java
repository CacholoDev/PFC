package com.pfcdaw.pfcdaw;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.pfcdaw.pfcdaw.controller.LoginUsuarioC;
import com.pfcdaw.pfcdaw.dto.LoginDto;
import com.pfcdaw.pfcdaw.exception.BusinessException;
import com.pfcdaw.pfcdaw.model.ClienteEntity;
import com.pfcdaw.pfcdaw.model.LoginRoleEnum;
import com.pfcdaw.pfcdaw.model.RefreshTokenEntity;
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
                .email("admin@panaderia.com") // ✅ Email real que existe en la BD
                .direccion("Calle Principal 123")
                .nombreEmpresa("Panaderia")
                .telefono("123456789")
                .password("$2a$10$N9qo8uLOickgx2ZMRZoMye") // Hash BCrypt real (admin123)
                .role(LoginRoleEnum.ADMIN)
                .build();

        // DTO con credenciales INCORRECTAS
        loginDto = new LoginDto();
        loginDto.setEmail("admin@panaderia.com");
        loginDto.setPassword("passwordIncorrecta"); // ❌ Password INCORRECTA
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

    @Test
    void testLoginExitoso() {
        // ========== ARRANGE: PREPARAR PARA LOGIN EXITOSO ==========
        
        // 1. Cambiar el LoginDto ANTES de los mocks
        //    (porque los mocks se van a usar para verificar qué se llamó)
        loginDto.setPassword("admin123/ñ");  // ✅ CONTRASEÑA CORRECTA
        
        // 2. Configurar los mocks para el caso EXITOSO
        when(limitesIntentosService.tryLogin("admin@panaderia.com"))
            .thenReturn(true);  // Usuario NO ESTÁ bloqueado
        
        when(clienteRepository.findByEmail("admin@panaderia.com"))
            .thenReturn(Optional.of(cliente));  // Encontramos el usuario
        
        when(passwordEncoder.matches("admin123/ñ", cliente.getPassword()))
            .thenReturn(true);  // ✅ LA CONTRASEÑA ES CORRECTA
        
        when(jwtTokenProvider.generateToken(cliente))
            .thenReturn("fake-jwt-token");  // Generamos un token fake
        
        // 3. Mock para RefreshTokenService
        //    createRefreshToken retorna una RefreshTokenEntity
        RefreshTokenEntity fakeRefreshToken = new RefreshTokenEntity();
        fakeRefreshToken.setToken("fake-refresh-token");
        
        when(refreshTokenService.createRefreshToken(cliente))
            .thenReturn(fakeRefreshToken);
        
        // ========== ACT: EJECUTAR EL LOGIN ==========
        ResponseEntity<Map<String, Object>> response = loginController.login(loginDto);
        
        // ========== ASSERT: VERIFICAR LOS RESULTADOS ==========
        
        // 1. Verificar que el status HTTP es 200 OK
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // 2. Extraer el body del ResponseEntity
        Map<String, Object> body = response.getBody();
        
        // 3. Verificar que el body NO es null
        assertNotNull(body);
        
        // 4. Verificar que tiene el token
        assertNotNull(body.get("token"));
        assertEquals("fake-jwt-token", body.get("token"));
        
        // 5. Verificar que tiene el refreshToken
        assertNotNull(body.get("refreshToken"));
        assertEquals("fake-refresh-token", body.get("refreshToken"));
        
        // 6. Verificar que los datos del usuario son correctos
        assertEquals("admin@panaderia.com", body.get("email"));
        assertEquals("Admin", body.get("nombre"));
        assertEquals("Panaderia", body.get("apellido"));
        assertEquals(1L, body.get("id"));
    }
}