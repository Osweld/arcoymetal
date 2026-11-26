package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.identityaccess.internal.application.command.LoginCommand;
import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.AuthenticatedSession;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.LoginUseCase;
import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.security.RefreshTokenCookieHelper;
import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.request.LoginRequest;
import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.response.AuthSessionResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginUseCase loginUseCase;
    private final RefreshTokenCookieHelper refreshTokenCookieHelper;

    @PostMapping("/login")
    public ResponseEntity<AuthSessionResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthenticatedSession session = loginUseCase.login(new LoginCommand(request.email(), request.password()));
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
