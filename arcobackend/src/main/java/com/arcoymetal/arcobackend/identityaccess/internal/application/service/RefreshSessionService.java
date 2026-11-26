package com.arcoymetal.arcobackend.identityaccess.internal.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.AuthenticatedSession;
import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.AuthenticatedUserView;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.RefreshSessionUseCase;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.TokenService;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.UserRepository;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.User;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.InvalidRefreshTokenException;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshSessionService implements RefreshSessionUseCase {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Override
    @Transactional(readOnly = true)
    public AuthenticatedSession refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidRefreshTokenException("Refresh token is missing.");
        }

        TokenService.RefreshTokenClaims claims = tokenService.parseAndValidateRefreshToken(refreshToken);

        User user = userRepository.findOptionalById(claims.userId())
                .orElseThrow(() -> new InvalidRefreshTokenException("User associated with refresh token not found."));

        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new InvalidRefreshTokenException("User account is not active.");
        }

        if (user.getTokenVersion() != claims.tokenVersion()) {
            throw new InvalidRefreshTokenException("Refresh token has been revoked.");
        }

        String newAccessToken = tokenService.generateAccessToken(user);
        String newRefreshToken = tokenService.generateRefreshToken(user);

        return new AuthenticatedSession(
                newAccessToken,
                newRefreshToken,
                tokenService.getAccessTokenExpirationSeconds(),
                new AuthenticatedUserView(
                        user.getUserId().value(),
                        user.getEmployeeId().value(),
                        user.getEmail().value(),
                        user.getRoles(),
                        user.getUserStatus(),
                        user.isMustChangePassword()
                )
        );
    }
}
