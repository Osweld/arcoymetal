package com.arcoymetal.arcobackend.identityaccess.internal.application.command;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Role;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserStatus;

public record UpdateUserCommand(
        UUID userId,
        String email,
        Set<Role> roles,
        UserStatus status
) {
    public UpdateUserCommand {
        Objects.requireNonNull(userId, "UserId cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(roles, "Roles cannot be null");
        Objects.requireNonNull(status, "UserStatus cannot be null");
    }
}
