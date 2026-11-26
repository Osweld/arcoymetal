package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.security;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "arco.security")
public record SecurityProperties(
        Jwt jwt,
        Cookie cookie,
        Cors cors,
        Login login
) {
    public SecurityProperties {
        if (jwt == null) {
            jwt = new Jwt(
                    "arco-metal-dev-secret-change-me-in-production-min-32-bytes-key!!",
                    900L,
                    604800L
            );
        }
        if (cookie == null) {
            cookie = new Cookie("arco_refresh_token", false, "Lax");
        }
        if (cors == null) {
            cors = new Cors(List.of("http://localhost:4200"));
        }
        if (login == null) {
            login = new Login(5);
        }
    }

    public record Jwt(
            String secret,
            long accessTokenExpirationSeconds,
            long refreshTokenExpirationSeconds
    ) {
        public Jwt {
            if (secret == null || secret.getBytes().length < 32) {
                throw new IllegalArgumentException("JWT secret must be at least 32 bytes long");
            }
            if (accessTokenExpirationSeconds <= 0) {
                accessTokenExpirationSeconds = 900L;
            }
            if (refreshTokenExpirationSeconds <= 0) {
                refreshTokenExpirationSeconds = 604800L;
            }
        }
    }

    public record Cookie(
            String refreshTokenName,
            boolean secure,
            String sameSite
    ) {
        public Cookie {
            if (refreshTokenName == null || refreshTokenName.isBlank()) {
                refreshTokenName = "arco_refresh_token";
            }
            if (sameSite == null || sameSite.isBlank()) {
                sameSite = "Lax";
            }
        }
    }

    public record Cors(List<String> allowedOrigins) {
        public Cors {
            if (allowedOrigins == null || allowedOrigins.isEmpty()) {
                allowedOrigins = List.of("http://localhost:4200");
            }
        }
    }

    public record Login(int maxFailedAttempts) {
        public Login {
            if (maxFailedAttempts < 1) {
                maxFailedAttempts = 5;
            }
        }
    }
}
