package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.request;

import java.util.Set;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Role;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 254, message = "Email must not exceed 254 characters")
        String email,

        @NotEmpty(message = "At least one role is required")
        Set<@NotNull(message = "Role cannot be null") Role> roles,

        @NotNull(message = "User status is required")
        UserStatus status
) {
}
