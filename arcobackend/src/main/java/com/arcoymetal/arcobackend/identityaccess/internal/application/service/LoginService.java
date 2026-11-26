package com.arcoymetal.arcobackend.identityaccess.internal.application.service;

import java.util.Locale;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.identityaccess.internal.application.command.LoginCommand;
import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.AuthenticatedSession;
import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.AuthenticatedUserView;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.LoginUseCase;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.PasswordHasher;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.TokenService;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.UserRepository;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.User;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.InvalidCredentialsException;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.UserAccountInactiveException;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserStatus;

@Service
public class LoginService implements LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;
    private final int maxFailedAttempts;

    public LoginService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            TokenService tokenService,
            @Value("${arco.security.login.max-failed-attempts:5}") int maxFailedAttempts
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenService = tokenService;
        this.maxFailedAttempts = maxFailedAttempts;
    }

    @Override
    @Transactional(noRollbackFor = InvalidCredentialsException.class)
    public AuthenticatedSession login(LoginCommand command) {
        Objects.requireNonNull(command, "LoginCommand cannot be null");

        String normalizedEmail = command.email().trim().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(InvalidCredentialsException::new);

        if (user.getUserStatus() == UserStatus.LOCKED) {
            throw new UserAccountInactiveException(
                    "La cuenta está bloqueada por múltiples intentos fallidos. Contacta a un administrador."
            );
        }

        if (user.getUserStatus() == UserStatus.DISABLED) {
            throw new UserAccountInactiveException(
                    "La cuenta se encuentra desactivada. Contacta a un administrador."
            );
        }

        if (!passwordHasher.matches(command.password(), user.getPasswordHash())) {
            user.recordFailedLoginAttempt(maxFailedAttempts);
            userRepository.save(user);
            throw new InvalidCredentialsException();
        }

        if (user.getFailedLoginAttempts() > 0) {
            user.recordSuccessfulLogin();
            userRepository.save(user);
        }

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        return new AuthenticatedSession(
                accessToken,
                refreshToken,
                tokenService.getAccessTokenExpirationSeconds(),
                toUserView(user)
        );
    }

    private static AuthenticatedUserView toUserView(User user) {
        return new AuthenticatedUserView(
                user.getUserId().value(),
                user.getEmployeeId().value(),
                user.getEmail().value(),
                user.getRoles(),
                user.getUserStatus(),
                user.isMustChangePassword()
        );
    }
}
