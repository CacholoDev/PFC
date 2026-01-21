package com.pfcdaw.pfcdaw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.pfcdaw.pfcdaw.security.JwtTokenFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Inyecta el filtro JWT que creamos
    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    // Bean para encriptar contraseñas con BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. CSRF: Deshabilitado (API REST stateless, no usa cookies de sesión)
            .csrf(csrf -> csrf.disable())
            
            // 2. SESIONES: Stateless (sin sesiones en servidor, usaremos JWT)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 3. CORS: Ya lo tienes configurado en WebConfig, esto lo mantiene
            .cors(cors -> cors.disable()) // Usa el CORS de WebConfig
            
            // 4. AUTORIZACIÓN: Qué endpoints son públicos y cuáles protegidos
            .authorizeHttpRequests(auth -> auth
                // PÚBLICOS (sin autenticación):
                .requestMatchers("/auth/**").permitAll()              // Login y registro
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger
                .requestMatchers("/error").permitAll()                // Páginas de error
                .requestMatchers("/*.html", "/css/**", "/js/**", "/assets/**", "/error_pages/**").permitAll() // Frontend estático
                
                // PROTEGIDOS (requieren autenticación):
                // Ahora sí activamos la protección: cualquier otra petición necesita JWT válido
                .anyRequest().authenticated()
            )
            
            // 5. AGREGAR EL FILTRO JWT: 
            // Lo añadimos ANTES del filtro de usuario/contraseña
            // Así el JWT se valida antes que cualquier otra autenticación
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 6. DESHABILITAR form login (no usamos formularios de Spring)
            //  No agregamos .formLogin() ni .logout() porque es una API REST
            ;

        return http.build();
    }

}
