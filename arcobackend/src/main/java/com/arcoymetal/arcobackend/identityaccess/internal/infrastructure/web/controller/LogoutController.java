package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.controller;

import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.LogoutUseCase;
import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.security.RefreshTokenCookieHelper;
import com.arcoymetal.arcobackend.shared.infrastructure.security.AuthenticatedUserPrincipal;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class LogoutController {

    private final LogoutUseCase logoutUseCase;
    private final RefreshTokenCookieHelper refreshTokenCookieHelper;

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        UUID authenticatedUserId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUserPrincipal principal) {
            authenticatedUserId = principal.userId();
        }

        String refreshToken = refreshTokenCookieHelper.extractRefreshToken(request);
        logoutUseCase.logout(authenticatedUserId, refreshToken);

        ResponseCookie clearCookie = refreshTokenCookieHelper.buildClearCookie();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .build();
    }
}
