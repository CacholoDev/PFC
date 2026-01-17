package com.pfcdaw.pfcdaw.security;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pfcdaw.pfcdaw.model.ClienteEntity;
import com.pfcdaw.pfcdaw.repository.ClienteRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(JwtTokenFilter.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final ClienteRepository clienteRepository;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, ClienteRepository clienteRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.clienteRepository = clienteRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extrae el header Authorization de la petición
        String authHeader = request.getHeader("Authorization");
        
        // Verifica si el header existe y tiene el prefijo "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            log.debug("Header Authorization encontrado");
            
            // Extrae el token (quita los 7 caracteres de "Bearer ")
            String token = authHeader.substring(7);
            
            // Valida que el token no esté vacío
            if (token.isEmpty()) {
                log.warn("Token JWT vacío en la cabecera Authorization");
            } else if (jwtTokenProvider.validateToken(token)) {
                // Token es válido, extrae información
                log.debug("Token JWT válido");
                
                try {
                    // Extrae el email (subject) del token
                    String email = jwtTokenProvider.getEmailFromToken(token);
                    
                    // Busca el cliente en la base de datos por email
                    ClienteEntity cliente = clienteRepository.findByEmail(email).orElse(null);
                    if (cliente == null) {
                        log.warn("Cliente no encontrado en BD para el email: {}", email);
                    } else {
                        log.debug("Cliente cargado desde BD: {} {}", cliente.getNombre(), cliente.getApellido());
                    }
                    
                    // Extrae el rol del token
                    String role = jwtTokenProvider.getRoleFromToken(token);
                    if (role == null) {
                        log.warn("Rol no encontrado en el token JWT para el email: {}", email);
                    }
                    
                    // Solo autentica si TANTO el cliente como el rol son válidos
                    if (cliente != null && role != null) {
                        // Construye el objeto Authentication con las autoridades (permisos)
                        var authentication = new UsernamePasswordAuthenticationToken(
                            cliente,  // Principal (el usuario)
                            null,     // Credentials (no necesario aquí, token ya validado)
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))  // Authorities (permisos)
                        );
                        
                        // Coloca el Authentication en el contexto de Spring Security
                        // Así el controller sabrá que el usuario está autenticado
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("Usuario autenticado exitosamente: {} con rol: {}", email, role);
                    }
                } catch (Exception e) {
                    // Si algo falla al procesar el token, loguea pero continúa
                    log.error("Error procesando el token JWT: {}", e.getMessage());
                }
            } else {
                // Token existe pero no es válido
                log.debug("Token JWT inválido o expirado");
            }
        } else {
            // No hay token en la petición (endpoint público)
            log.debug("Sin autenticación JWT (endpoint público)");
        }
        
        // Continúa la cadena de filtros (SIEMPRE debe ocurrir)
        try {
            filterChain.doFilter(request, response);
        } catch (ServletException | IOException e) {
            log.error("Error en la cadena de filtros: {}", e.getMessage());
            throw e;
        }
    }

}
