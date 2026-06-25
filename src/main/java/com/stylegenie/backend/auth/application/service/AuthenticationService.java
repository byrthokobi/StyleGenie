package com.stylegenie.backend.auth.application.service;

import com.stylegenie.backend.auth.application.dto.*;
import com.stylegenie.backend.auth.application.exception.AccountDeactivatedException;
import com.stylegenie.backend.auth.application.exception.EmailAlreadyExistsException;
import com.stylegenie.backend.auth.application.exception.InvalidCredentialsException;
import com.stylegenie.backend.auth.domain.entity.RefreshToken;
import com.stylegenie.backend.auth.domain.entity.User;
import com.stylegenie.backend.auth.domain.repository.UserRepository;
import com.stylegenie.backend.auth.domain.vo.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordService passwordService,
            TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String email = request.email().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordService.hash(request.password()));
        user.setRole(Role.REGULAR_USER);

        User saved = userRepository.save(user);
        return new RegisterResponse(saved.getPublicId(), saved.getEmail(), saved.getRole());
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String email = request.email().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive()) {
            throw new AccountDeactivatedException();
        }

        if (!passwordService.verify(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = tokenService.generateAccessToken(user);
        RefreshToken refreshToken = tokenService.generateRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken.getToken(), "Bearer", 900L);
    }

    @Transactional
    public void logout(LogoutRequest request) {
        tokenService.revokeRefreshToken(request.refreshToken());
    }
}
