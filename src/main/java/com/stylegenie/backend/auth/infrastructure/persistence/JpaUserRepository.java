package com.stylegenie.backend.auth.infrastructure.persistence;

import com.stylegenie.backend.auth.domain.entity.User;
import com.stylegenie.backend.auth.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<User, Long>, UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findByPublicId(UUID publicId);
    boolean existsByEmail(String email);
}
