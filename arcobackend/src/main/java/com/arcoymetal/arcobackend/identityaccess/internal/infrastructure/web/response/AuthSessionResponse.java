package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.response;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.AuthenticatedUserView;

public record AuthSessionResponse(
        String accessToken,
        long expiresInSeconds,
        AuthenticatedUserView user
) {
}
