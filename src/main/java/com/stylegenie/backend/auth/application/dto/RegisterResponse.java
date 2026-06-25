package com.stylegenie.backend.auth.application.dto;

import com.stylegenie.backend.auth.domain.vo.Role;

import java.util.UUID;

public record RegisterResponse(
        UUID publicId,
        String email,
        Role role
) {}
