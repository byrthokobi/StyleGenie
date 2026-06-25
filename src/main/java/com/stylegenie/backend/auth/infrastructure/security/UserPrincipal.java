package com.stylegenie.backend.auth.infrastructure.security;

import java.util.UUID;

public record UserPrincipal(Long userId, UUID publicId, String role) {}
