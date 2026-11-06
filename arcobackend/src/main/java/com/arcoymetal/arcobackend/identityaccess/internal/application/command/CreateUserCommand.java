package com.arcoymetal.arcobackend.identityaccess.internal.application.command;

import java.util.Set;
import java.util.UUID;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Role;

public record CreateUserCommand(
        String password,
        String email,
        UUID employeeId,
        Set<Role> roles
) {

}
