package com.arcoymetal.arcobackend.identityaccess.internal.application.dto;

public record AuthenticatedSession(
        String accessToken,
        String refreshToken,
        long expiresInSeconds,
        AuthenticatedUserView user
) {
}
