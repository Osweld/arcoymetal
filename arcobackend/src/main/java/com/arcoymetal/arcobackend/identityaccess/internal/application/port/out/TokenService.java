package com.arcoymetal.arcobackend.identityaccess.internal.application.port.out;

import java.util.Set;
import java.util.UUID;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.User;

public interface TokenService {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    AccessTokenClaims parseAndValidateAccessToken(String token);

    RefreshTokenClaims parseAndValidateRefreshToken(String token);

    long getAccessTokenExpirationSeconds();

    long getRefreshTokenExpirationSeconds();

    record AccessTokenClaims(
            UUID userId,
            UUID employeeId,
            String email,
            Set<String> roles,
            long tokenVersion,
            boolean mustChangePassword
    ) {
    }

    record RefreshTokenClaims(
            UUID userId,
            long tokenVersion
    ) {
    }
}
