package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.AuthenticatedSession;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.RefreshSessionUseCase;
import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.security.RefreshTokenCookieHelper;
import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.response.AuthSessionResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshSessionUseCase refreshSessionUseCase;
    private final RefreshTokenCookieHelper refreshTokenCookieHelper;

    @PostMapping("/refresh")
    public ResponseEntity<AuthSessionResponse> refresh(HttpServletRequest request) {
        String refreshToken = refreshTokenCookieHelper.extractRefreshToken(request);
        AuthenticatedSession session = refreshSessionUseCase.refresh(refreshToken);
        ResponseCookie cookie = refreshTokenCookieHelper.buildRefreshTokenCookie(session.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthSessionResponse(
                        session.accessToken(),
                        session.expiresInSeconds(),
                        session.user()
                ));
    }
}
