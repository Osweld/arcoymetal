package com.arcoymetal.arcobackend.identityaccess.internal.application.dto;

import java.util.Set;
import java.util.UUID;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Role;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserStatus;

public record AuthenticatedUserView(
        UUID id,
        UUID employeeId,
        String email,
        Set<Role> roles,
        UserStatus status,
        boolean mustChangePassword
) {
}
