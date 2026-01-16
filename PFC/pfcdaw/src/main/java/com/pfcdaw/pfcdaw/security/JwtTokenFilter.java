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

    Logger log = org.slf4j.LoggerFactory.getLogger(JwtTokenFilter.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final ClienteRepository clienteRepository;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, ClienteRepository clienteRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.clienteRepository = clienteRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if(token.isEmpty()) {
                log.warn("Token JWT vacío en la cabecera Authorization");
                return;
            }
            if (jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.getEmailFromToken(token);
                // Aquí podrías cargar el cliente desde la base de datos si es necesario
                ClienteEntity cliente = clienteRepository.findByEmail(email).orElse(null);
                String role = jwtTokenProvider.getRoleFromToken(token);

                //cliente not null
                if(cliente == null) {
                   log.warn("Cliente no encontrado para el email del token: {}", email);
                }
                // Configurar el contexto de seguridad si usas Spring Security
                //no olvidar role
                try {
                if (cliente != null) {
                    // Aquí podrías establecer la autenticación en el contexto de seguridad
                var authentication = new UsernamePasswordAuthenticationToken(cliente, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));    

                SecurityContextHolder.getContext().setAuthentication(authentication);

                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            }
        }
        filterChain.doFilter(request, response);

    }

    

}
