package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.TokenService;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.User;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.InvalidRefreshTokenException;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Email;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.EmployeeId;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.PasswordHash;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Role;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserId;

import tools.jackson.databind.ObjectMapper;

class HmacJwtTokenServiceTest {

    private static final String SECRET = "test-secret-key-for-hmac-sha256-at-least-32-bytes!";

    private SecurityProperties createProperties(long accessTtl, long refreshTtl) {
        return new SecurityProperties(
                new SecurityProperties.Jwt(SECRET, accessTtl, refreshTtl),
                new SecurityProperties.Cookie("arco_refresh_token", false, "Lax"),
                new SecurityProperties.Cors(List.of("http://localhost:4200")),
                new SecurityProperties.Login(5)
        );
    }

    private User sampleUser() {
        return User.create(
                UserId.generate(),
                EmployeeId.generate(),
                new PasswordHash("a".repeat(60)),
                new Email("admin@arcoymetal.com.sv"),
                Set.of(Role.ADMINISTRATOR, Role.MANAGER)
        );
    }

    @Test
    void shouldGenerateAndValidateAccessAndRefreshTokens() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-11-25T12:00:00Z"), ZoneOffset.UTC);
        HmacJwtTokenService tokenService = new HmacJwtTokenService(
                createProperties(900, 604800),
                new ObjectMapper(),
                fixedClock
        );
        User user = sampleUser();

        String accessToken = tokenService.generateAccessToken(user);
        TokenService.AccessTokenClaims accessClaims = tokenService.parseAndValidateAccessToken(accessToken);

        assertThat(accessClaims.userId()).isEqualTo(user.getUserId().value());
        assertThat(accessClaims.employeeId()).isEqualTo(user.getEmployeeId().value());
        assertThat(accessClaims.email()).isEqualTo("admin@arcoymetal.com.sv");
        assertThat(accessClaims.roles()).containsExactlyInAnyOrder("ADMINISTRATOR", "MANAGER");
        assertThat(accessClaims.tokenVersion()).isZero();
        assertThat(accessClaims.mustChangePassword()).isFalse();

        String refreshToken = tokenService.generateRefreshToken(user);
        TokenService.RefreshTokenClaims refreshClaims = tokenService.parseAndValidateRefreshToken(refreshToken);

        assertThat(refreshClaims.userId()).isEqualTo(user.getUserId().value());
        assertThat(refreshClaims.tokenVersion()).isZero();

        // Cross-type usage rejected
        assertThatThrownBy(() -> tokenService.parseAndValidateAccessToken(refreshToken))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> tokenService.parseAndValidateRefreshToken(accessToken))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void shouldRejectTamperedOrExpiredTokens() {
        Instant t0 = Instant.parse("2026-11-25T12:00:00Z");
        HmacJwtTokenService issuer = new HmacJwtTokenService(
                createProperties(60, 120),
                new ObjectMapper(),
                Clock.fixed(t0, ZoneOffset.UTC)
        );
        User user = sampleUser();
        String accessToken = issuer.generateAccessToken(user);
        String refreshToken = issuer.generateRefreshToken(user);

        // Tampered signature
        String tampered = accessToken.substring(0, accessToken.length() - 2) + "xx";
        assertThatThrownBy(() -> issuer.parseAndValidateAccessToken(tampered))
                .isInstanceOf(IllegalArgumentException.class);

        // Expired after 180 seconds
        HmacJwtTokenService verifierLater = new HmacJwtTokenService(
                createProperties(60, 120),
                new ObjectMapper(),
                Clock.fixed(t0.plusSeconds(180), ZoneOffset.UTC)
        );
        assertThatThrownBy(() -> verifierLater.parseAndValidateAccessToken(accessToken))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> verifierLater.parseAndValidateRefreshToken(refreshToken))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }
}
