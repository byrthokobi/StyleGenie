package com.stylegenie.backend.auth.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stylegenie.backend.auth.application.dto.*;
import com.stylegenie.backend.auth.application.exception.AccountDeactivatedException;
import com.stylegenie.backend.auth.application.exception.EmailAlreadyExistsException;
import com.stylegenie.backend.auth.application.exception.InvalidCredentialsException;
import com.stylegenie.backend.auth.application.service.AuthenticationService;
import com.stylegenie.backend.auth.domain.vo.Role;
import com.stylegenie.backend.auth.infrastructure.security.JwtAuthenticationFilter;
import com.stylegenie.backend.auth.infrastructure.security.SecurityConfig;
import com.stylegenie.backend.auth.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean AuthenticationService authenticationService;
    @MockitoBean JwtUtil jwtUtil;

    // --- POST /auth/register ---

    @Test
    void register_validRequest_returns201() throws Exception {
        UUID publicId = UUID.randomUUID();
        when(authenticationService.register(any()))
                .thenReturn(new RegisterResponse(publicId, "user@example.com", Role.REGULAR_USER));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@example.com","password":"password123"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.role").value("REGULAR_USER"))
                .andExpect(jsonPath("$.publicId").value(publicId.toString()));
    }

    @Test
    void register_invalidEmail_returns400() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"not-an-email","password":"password123"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void register_shortPassword_returns400() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@example.com","password":"short"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void register_duplicateEmail_returns409() throws Exception {
        when(authenticationService.register(any())).thenThrow(new EmailAlreadyExistsException());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@example.com","password":"password123"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email already in use"));
    }

    // --- POST /auth/login ---

    @Test
    void login_validCredentials_returns200WithTokens() throws Exception {
        when(authenticationService.login(any()))
                .thenReturn(new LoginResponse("jwt-token", "refresh-uuid", "Bearer", 900L));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@example.com","password":"password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-uuid"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        when(authenticationService.login(any())).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@example.com","password":"wrongpass"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }

    @Test
    void login_deactivatedAccount_returns401() throws Exception {
        when(authenticationService.login(any())).thenThrow(new AccountDeactivatedException());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@example.com","password":"password123"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Account is deactivated"));
    }

    // --- POST /auth/logout ---

    @Test
    void logout_validToken_returns200() throws Exception {
        doNothing().when(authenticationService).logout(any());

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"valid-refresh-uuid"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }

    @Test
    void logout_missingToken_returns400() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }
}
