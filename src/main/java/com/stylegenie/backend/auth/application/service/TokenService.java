package com.stylegenie.backend.auth.application.service;

import com.stylegenie.backend.auth.application.exception.InvalidTokenException;
import com.stylegenie.backend.auth.domain.entity.RefreshToken;
import com.stylegenie.backend.auth.domain.entity.User;
import com.stylegenie.backend.auth.domain.repository.RefreshTokenRepository;
import com.stylegenie.backend.auth.infrastructure.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class TokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenExpiryMs;

    public TokenService(
            JwtUtil jwtUtil,
            RefreshTokenRepository refreshTokenRepository,
            @Value("${app.jwt.refresh-token-expiry-ms}") long refreshTokenExpiryMs
    ) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenExpiryMs = refreshTokenExpiryMs;
    }

    public String generateAccessToken(User user) {
        return jwtUtil.generateAccessToken(user);
    }

    @Transactional
    public RefreshToken generateRefreshToken(User user) {
        RefreshToken token = new RefreshToken();
        token.setUserId(user.getId());
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(Instant.now().plusMillis(refreshTokenExpiryMs));
        token.setRevoked(false);
        return refreshTokenRepository.save(token);
    }

    @Transactional
    public void revokeRefreshToken(String rawToken) {
        RefreshToken token = refreshTokenRepository.findByToken(rawToken)
                .orElseThrow(InvalidTokenException::new);

        if (token.isRevoked() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidTokenException();
        }

        refreshTokenRepository.revokeByToken(rawToken);
    }

    @Transactional
    public void revokeAllTokensForUser(Long userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }
}
