package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.security;

import java.time.Duration;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RefreshTokenCookieHelper {

    private static final String COOKIE_PATH = "/api/v1/auth";

    private final SecurityProperties securityProperties;

    public ResponseCookie buildRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(securityProperties.cookie().refreshTokenName(), refreshToken)
                .httpOnly(true)
                .secure(securityProperties.cookie().secure())
                .sameSite(securityProperties.cookie().sameSite())
                .path(COOKIE_PATH)
                .maxAge(Duration.ofSeconds(securityProperties.jwt().refreshTokenExpirationSeconds()))
                .build();
    }

    public ResponseCookie buildClearCookie() {
        return ResponseCookie.from(securityProperties.cookie().refreshTokenName(), "")
                .httpOnly(true)
                .secure(securityProperties.cookie().secure())
                .sameSite(securityProperties.cookie().sameSite())
                .path(COOKIE_PATH)
                .maxAge(0)
                .build();
    }

    public String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        String cookieName = securityProperties.cookie().refreshTokenName();
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
