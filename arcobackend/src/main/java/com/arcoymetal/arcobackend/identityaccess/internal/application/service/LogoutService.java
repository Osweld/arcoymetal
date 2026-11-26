package com.arcoymetal.arcobackend.identityaccess.internal.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.LogoutUseCase;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.TokenService;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.UserRepository;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.InvalidRefreshTokenException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutUseCase {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Override
    @Transactional
    public void logout(UUID authenticatedUserId, String refreshToken) {
        UUID targetUserId = authenticatedUserId;

        if (targetUserId == null && refreshToken != null && !refreshToken.isBlank()) {
            try {
                targetUserId = tokenService.parseAndValidateRefreshToken(refreshToken).userId();
            } catch (InvalidRefreshTokenException ignored) {
                return;
            }
        }

        if (targetUserId == null) {
            return;
        }

        userRepository.findOptionalById(targetUserId).ifPresent(user -> {
            user.revokeSessions();
            userRepository.save(user);
        });
    }
}
