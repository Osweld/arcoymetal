package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.TokenService;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.User;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.InvalidRefreshTokenException;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class HmacJwtTokenService implements TokenService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String HEADER_JSON = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private static final String ENCODED_HEADER = Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(HEADER_JSON.getBytes(StandardCharsets.UTF_8));

    private final byte[] secretBytes;
    private final long accessTokenExpirationSeconds;
    private final long refreshTokenExpirationSeconds;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public HmacJwtTokenService(
            SecurityProperties securityProperties,
            ObjectMapper objectMapper,
            Clock clock
    ) {
        this.secretBytes = securityProperties.jwt().secret().getBytes(StandardCharsets.UTF_8);
        this.accessTokenExpirationSeconds = securityProperties.jwt().accessTokenExpirationSeconds();
        this.refreshTokenExpirationSeconds = securityProperties.jwt().refreshTokenExpirationSeconds();
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    @Override
    public String generateAccessToken(User user) {
        Instant now = Instant.now(clock);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", user.getUserId().value().toString());
        payload.put("empId", user.getEmployeeId().value().toString());
        payload.put("email", user.getEmail().value());
        payload.put("roles", user.getRoles().stream().map(Enum::name).sorted().toList());
        payload.put("tv", user.getTokenVersion());
        payload.put("mcp", user.isMustChangePassword());
        payload.put("typ", "access");
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", now.plusSeconds(accessTokenExpirationSeconds).getEpochSecond());
        return signPayload(payload);
    }

    @Override
    public String generateRefreshToken(User user) {
        Instant now = Instant.now(clock);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", user.getUserId().value().toString());
        payload.put("tv", user.getTokenVersion());
        payload.put("typ", "refresh");
        payload.put("jti", UUID.randomUUID().toString());
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", now.plusSeconds(refreshTokenExpirationSeconds).getEpochSecond());
        return signPayload(payload);
    }

    @Override
    public AccessTokenClaims parseAndValidateAccessToken(String token) {
        JsonNode payload = verifyAndDecodePayload(token, "access");
        try {
            UUID userId = UUID.fromString(payload.get("sub").asString());
            UUID employeeId = UUID.fromString(payload.get("empId").asString());
            String email = payload.get("email").asString();
            long tokenVersion = payload.get("tv").asLong();
            boolean mustChangePassword = payload.has("mcp") && payload.get("mcp").asBoolean();

            Set<String> roles = new HashSet<>();
            JsonNode rolesNode = payload.get("roles");
            if (rolesNode != null && rolesNode.isArray()) {
                for (JsonNode roleNode : rolesNode) {
                    roles.add(roleNode.asString());
                }
            }
            if (roles.isEmpty()) {
                throw new IllegalArgumentException("Access token has no roles");
            }

            return new AccessTokenClaims(userId, employeeId, email, Set.copyOf(roles), tokenVersion, mustChangePassword);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Invalid access token claims", ex);
        }
    }

    @Override
    public RefreshTokenClaims parseAndValidateRefreshToken(String token) {
        try {
            JsonNode payload = verifyAndDecodePayload(token, "refresh");
            UUID userId = UUID.fromString(payload.get("sub").asString());
            long tokenVersion = payload.get("tv").asLong();
            return new RefreshTokenClaims(userId, tokenVersion);
        } catch (RuntimeException ex) {
            throw new InvalidRefreshTokenException("Refresh token is invalid or expired.");
        }
    }

    @Override
    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationSeconds;
    }

    @Override
    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenExpirationSeconds;
    }

    private String signPayload(Map<String, Object> payload) {
        String payloadJson = objectMapper.writeValueAsString(payload);
        String encodedPayload = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        String signingInput = ENCODED_HEADER + "." + encodedPayload;
        byte[] signature = computeHmac(signingInput);
        String encodedSignature = Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        return signingInput + "." + encodedSignature;
    }

    private JsonNode verifyAndDecodePayload(String token, String expectedType) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be blank");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Malformed JWT");
        }

        String signingInput = parts[0] + "." + parts[1];
        byte[] expectedSignature = computeHmac(signingInput);
        byte[] providedSignature = Base64.getUrlDecoder().decode(parts[2]);

        if (!MessageDigest.isEqual(expectedSignature, providedSignature)) {
            throw new IllegalArgumentException("Invalid JWT signature");
        }

        byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
        JsonNode payload = objectMapper.readTree(new String(payloadBytes, StandardCharsets.UTF_8));

        String type = payload.has("typ") ? payload.get("typ").asString() : "";
        if (!expectedType.equals(type)) {
            throw new IllegalArgumentException("Unexpected JWT type");
        }

        long exp = payload.has("exp") ? payload.get("exp").asLong() : 0L;
        if (Instant.now(clock).getEpochSecond() >= exp) {
            throw new IllegalArgumentException("JWT has expired");
        }

        return payload;
    }

    private byte[] computeHmac(String signingInput) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secretBytes, HMAC_ALGORITHM));
            return mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new IllegalStateException("Unable to compute HMAC-SHA256 signature", ex);
        }
    }
}
