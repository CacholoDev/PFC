package com.pfcdaw.pfcdaw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Deshabilitar CSRF (Cross-Site Request Forgery)
                .csrf(csrf -> csrf.disable())
                    // 2. Configurar autorización de peticiones HTTP
                .authorizeHttpRequests((requests) -> requests
                    // 3. Definir qué endpoints son públicos
                                .requestMatchers("/css/**", "/js/**", "/images/**", "/register","/login").permitAll()
                                    // 4. El resto de endpoints requieren autenticación
                                .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                                .loginPage("/login")
                                .permitAll()
                )
                .logout((logout) -> logout.permitAll() // 5. Añadir nuestro filtro JWT personalizado
    
    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class));

        return http.build();
    }

}
