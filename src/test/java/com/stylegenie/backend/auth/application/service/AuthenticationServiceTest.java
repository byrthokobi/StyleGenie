package com.stylegenie.backend.auth.application.service;

import com.stylegenie.backend.auth.application.dto.*;
import com.stylegenie.backend.auth.application.exception.AccountDeactivatedException;
import com.stylegenie.backend.auth.application.exception.EmailAlreadyExistsException;
import com.stylegenie.backend.auth.application.exception.InvalidCredentialsException;
import com.stylegenie.backend.auth.domain.entity.RefreshToken;
import com.stylegenie.backend.auth.domain.entity.User;
import com.stylegenie.backend.auth.domain.repository.UserRepository;
import com.stylegenie.backend.auth.domain.vo.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordService passwordService;
    @Mock private TokenService tokenService;

    @InjectMocks private AuthenticationService authenticationService;

    private User activeUser;

    @BeforeEach
    void setUp() {
        activeUser = new User();
        activeUser.setEmail("user@example.com");
        activeUser.setPasswordHash("$2a$10$hashedpassword");
        activeUser.setRole(Role.REGULAR_USER);
        activeUser.setActive(true);
        setField(activeUser, "id", 1L);
        setField(activeUser, "publicId", UUID.randomUUID());
    }

    // --- register ---

    @Test
    void register_success_returnsResponse() {
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordService.hash("password123")).thenReturn("$2a$10$hash");
        when(userRepository.save(any(User.class))).thenReturn(activeUser);

        RegisterResponse response = authenticationService.register(
                new RegisterRequest("user@example.com", "password123")
        );

        assertThat(response.email()).isEqualTo("user@example.com");
        assertThat(response.role()).isEqualTo(Role.REGULAR_USER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_emailNormalizedToLowercase() {
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordService.hash(anyString())).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenReturn(activeUser);

        authenticationService.register(new RegisterRequest("USER@EXAMPLE.COM", "password123"));

        verify(userRepository).existsByEmail("user@example.com");
    }

    @Test
    void register_duplicateEmail_throwsEmailAlreadyExistsException() {
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        assertThatThrownBy(() ->
                authenticationService.register(new RegisterRequest("user@example.com", "password123"))
        ).isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    // --- login ---

    @Test
    void login_success_returnsTokenPair() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-uuid");
        refreshToken.setExpiresAt(Instant.now().plusSeconds(604800));

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(activeUser));
        when(passwordService.verify("password123", activeUser.getPasswordHash())).thenReturn(true);
        when(tokenService.generateAccessToken(activeUser)).thenReturn("jwt-token");
        when(tokenService.generateRefreshToken(activeUser)).thenReturn(refreshToken);

        LoginResponse response = authenticationService.login(
                new LoginRequest("user@example.com", "password123")
        );

        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-uuid");
        assertThat(response.tokenType()).isEqualTo("Bearer");
    }

    @Test
    void login_emailNotFound_throwsInvalidCredentialsException() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                authenticationService.login(new LoginRequest("unknown@example.com", "password123"))
        ).isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_wrongPassword_throwsInvalidCredentialsException() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(activeUser));
        when(passwordService.verify("wrongpass", activeUser.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() ->
                authenticationService.login(new LoginRequest("user@example.com", "wrongpass"))
        ).isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_deactivatedAccount_throwsAccountDeactivatedException() {
        activeUser.setActive(false);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(activeUser));

        assertThatThrownBy(() ->
                authenticationService.login(new LoginRequest("user@example.com", "password123"))
        ).isInstanceOf(AccountDeactivatedException.class);

        verify(passwordService, never()).verify(anyString(), anyString());
    }

    // --- logout ---

    @Test
    void logout_validToken_revokesToken() {
        doNothing().when(tokenService).revokeRefreshToken("valid-refresh-token");

        authenticationService.logout(new LogoutRequest("valid-refresh-token"));

        verify(tokenService).revokeRefreshToken("valid-refresh-token");
    }

    // --- helpers ---

    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
