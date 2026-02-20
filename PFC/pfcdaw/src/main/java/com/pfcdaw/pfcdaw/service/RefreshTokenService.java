package com.pfcdaw.pfcdaw.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pfcdaw.pfcdaw.exception.BusinessException;
import com.pfcdaw.pfcdaw.model.ClienteEntity;
import com.pfcdaw.pfcdaw.model.RefreshTokenEntity;
import com.pfcdaw.pfcdaw.repository.RefreshTokenRepository;
import com.pfcdaw.pfcdaw.security.JwtTokenProvider;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.jwt.refresh-expiration}")
    private Long refreshExpirationMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtTokenProvider jwtTokenProvider) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public RefreshTokenEntity createRefreshToken(ClienteEntity cliente) {
        String token = jwtTokenProvider.generateRefreshToken(cliente);
        LocalDateTime expiryDate = LocalDateTime.now().plus(Duration.ofMillis(refreshExpirationMs));

        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .cliente(cliente)
                .token(token)
                .expiryDate(expiryDate)
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshTokenEntity validateRefreshToken(String token) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Refresh token invalido"));

        if (refreshToken.isRevoked()) {
            throw new BusinessException("Refresh token revocado");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new BusinessException("Refresh token expirado");
        }

        return refreshToken;
    }

    public RefreshTokenEntity rotateRefreshToken(RefreshTokenEntity currentToken) {
        currentToken.setRevoked(true);
        refreshTokenRepository.save(currentToken);

        return createRefreshToken(currentToken.getCliente());
    }

    public void revokeAllTokensByClienteId(Long clienteId) {
        refreshTokenRepository.revokeAllTokensByClienteId(clienteId);
    }
}
