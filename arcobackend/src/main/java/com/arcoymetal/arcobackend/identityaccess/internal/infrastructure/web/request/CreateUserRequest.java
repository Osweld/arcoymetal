package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.request;

import java.util.Set;
import java.util.UUID;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank @Size(min = 8, max = 20) String password,
        @NotBlank @Email @Size(min = 5, max = 254) String email,
        @NotNull UUID employeeId,
        @NotNull Set<Role> roles) {

}
