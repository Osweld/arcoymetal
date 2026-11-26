package com.arcoymetal.arcobackend.shared.infrastructure.security;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record AuthenticatedUserPrincipal(
        UUID userId,
        UUID employeeId,
        String email,
        Set<String> roles,
        boolean mustChangePassword
) {
    public AuthenticatedUserPrincipal {
        Objects.requireNonNull(userId, "UserId cannot be null");
        Objects.requireNonNull(employeeId, "EmployeeId cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        roles = roles == null ? Set.of() : Set.copyOf(roles);
    }
}
