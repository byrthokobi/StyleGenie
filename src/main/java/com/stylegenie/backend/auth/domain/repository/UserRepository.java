package com.stylegenie.backend.auth.domain.repository;

import com.stylegenie.backend.auth.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    Optional<User> findByPublicId(UUID publicId);
    boolean existsByEmail(String email);
}
