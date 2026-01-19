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

    /**
     * Intercepta CADA petición HTTP antes de llegar al controller
     * 
     * FLUJO:
     * 1. Cliente envía request CON token JWT en header Authorization
     * 2. Este filtro EXTRAE el token, lo VALIDA (firma y expiración)
     * 3. Si es válido, CARGA el usuario de BD y lo AUTENTICA en Spring Security
     * 4. Controller recibe request CON el usuario ya autenticado en SecurityContext
     * 5. Si no hay token o es inválido, el request continúa sin autenticar
     *    (luego SecurityConfig rechazará con 401 si el endpoint es .authenticated())
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // ====== PASO 1: EXTRAE EL TOKEN DEL HEADER ======
        // El cliente envía: Authorization: Bearer <token_muy_largo_aqui>
        // Buscamos el header Authorization completo
        String authHeader = request.getHeader("Authorization");
        
        // Valida que: 1) el header exista, 2) comience con "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            log.debug("✓ Header Authorization encontrado en la petición");
            
            // Extrae solo el token (quita los 7 caracteres de "Bearer ")
            // De "Bearer abc123..." obtenemos "abc123..."
            String token = authHeader.substring(7);
            
            // Verifica que el token no sea una cadena vacía
            if (token.isEmpty()) {
                log.warn("⚠️ Token JWT vacío (header Authorization presente pero sin valor)");
            } 
            // ====== PASO 2: VALIDA LA FIRMA Y EXPIRACIÓN DEL TOKEN ======
            else if (jwtTokenProvider.validateToken(token)) {
                // validateToken() usa la CLAVE SECRETA para verificar que:
                // - La firma sea correcta (no fue modificado en tránsito)
                // - El token no haya expirado
                log.debug("✓ Token JWT válido (firma correcta y no expirado)");
                
                try {
                    // ====== PASO 3: EXTRAE INFO DEL TOKEN ======
                    // El token contiene:
                    // - subject (email del usuario que lo generó)
                    // - claim "role" (ADMIN o USER)
                    // - claim "id" (identificador del cliente)
                    String email = jwtTokenProvider.getEmailFromToken(token);
                    log.debug("Email extraído del token: {}", email);
                    
                    // ====== PASO 4: BUSCA EL USUARIO EN LA BD ======
                    // Aunque el token ya tiene los datos, lo buscamos en BD para:
                    // - Verificar que el usuario sigue existiendo (no fue eliminado)
                    // - Obtener la entidad ClienteEntity completa (nombre, apellido, etc.)
                    // - Poder pasar la entidad al controller si la necesita
                    ClienteEntity cliente = clienteRepository.findByEmail(email).orElse(null);
                    if (cliente == null) {
                        log.warn("⚠️ Usuario del token NO encontrado en BD: {}", email);
                        // No autenticamos (cliente = null), pero continuamos el filtro
                    } else {
                        log.debug("✓ Usuario cargado desde BD: {} {}", cliente.getNombre(), cliente.getApellido());
                    }
                    
                    // ====== PASO 5: EXTRAE EL ROL DEL TOKEN ======
                    // El rol también viene en el token, pero lo extraemos para autenticar
                    String role = jwtTokenProvider.getRoleFromToken(token);
                    if (role == null) {
                        log.warn("⚠️ Rol NO encontrado en el token para: {}", email);
                    }
                    
                    // ====== PASO 6: AUTENTICA EN SPRING SECURITY ======
                    // SOLO si AMBOS (cliente y rol) son válidos
                    // Si uno falla, no autenticamos y el request sigue sin autenticar
                    if (cliente != null && role != null) {
                        // Crea el objeto Authentication:
                        // - principal = ClienteEntity (el usuario logueado)
                        // - credentials = null (no necesario, ya validamos el token)
                        // - authorities = lista de permisos ("ROLE_ADMIN" o "ROLE_USER")
                        var authentication = new UsernamePasswordAuthenticationToken(
                            cliente,  // Principal: quién es el usuario
                            null,     // Credentials: prueba de quién es (token ya validado)
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))  // Authorities: qué puede hacer
                        );
                        
                        // ====== PASO 7: GUARDA EL USUARIO EN SPRING SECURITY ======
                        // Coloca el Authentication en el SecurityContextHolder
                        // (contexto local del thread de esta petición)
                        // 
                        // Ahora CUALQUIER CLASS que haga:
                        //   SecurityContextHolder.getContext().getAuthentication()
                        // obtendrá el usuario autenticado
                        //
                        // Por ejemplo, en un controller puedes hacer:
                        //   @GetMapping("/mi-perfil")
                        //   public ResponseEntity<?> miPerfil() {
                        //       ClienteEntity usuario = (ClienteEntity) SecurityContextHolder.getContext()
                        //                                  .getAuthentication().getPrincipal();
                        //       return ResponseEntity.ok(usuario);
                        //   }
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("✓ Usuario autenticado en Spring Security: {} (rol: {})", email, role);
                    }
                } catch (Exception e) {
                    // Si algo falla al procesar el token (parsear claims, etc.)
                    // Loguea el error pero CONTINÚA la cadena de filtros
                    // (No cierres la conexión, solo no autenticas)
                    log.error("❌ Error procesando el token JWT: {}", e.getMessage());
                }
            } 
            // Token existe en el header pero la validación falló
            else {
                log.debug("❌ Token JWT inválido o expirado (firma incorrecta o fecha vencida)");
            }
        } 
        // No hay token en la petición (es normal para endpoints públicos)
        else {
            log.debug("ℹ️ Sin autenticación JWT (endpoint público o sin token)");
        }
        
        // ====== PASO 8: CONTINÚA LA CADENA DE FILTROS ======
        // SIEMPRE se ejecuta, independientemente de si autenticamos o no
        // 
        // La cadena continúa así:
        // 1. JwtTokenFilter (AQUÍ) - Autentica si hay token válido
        // 2. Otros filtros de Spring Security...
        // 3. SecurityConfig verifica permisos (.authenticated(), .hasRole(), etc.)
        // 4. Controller
        // 5. Response
        try {
            filterChain.doFilter(request, response);
        } catch (ServletException | IOException e) {
            log.error("❌ Error en la cadena de filtros: {}", e.getMessage());
            throw e;
        }
    }

}
