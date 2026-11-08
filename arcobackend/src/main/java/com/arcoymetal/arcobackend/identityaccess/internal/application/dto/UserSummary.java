package com.arcoymetal.arcobackend.identityaccess.internal.application.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Role;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserStatus;

public record UserSummary(
    UUID id,
    UUID employeeId,
    String email,
    Set<Role> roles,
    UserStatus status,
    Instant createdAt) {

}
