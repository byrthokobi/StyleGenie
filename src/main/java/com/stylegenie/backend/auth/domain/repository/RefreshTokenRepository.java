package com.stylegenie.backend.auth.domain.repository;

import com.stylegenie.backend.auth.domain.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken token);
    Optional<RefreshToken> findByToken(String token);
    void revokeByToken(String token);
    void revokeAllByUserId(Long userId);
    void deleteAllExpired();
}
