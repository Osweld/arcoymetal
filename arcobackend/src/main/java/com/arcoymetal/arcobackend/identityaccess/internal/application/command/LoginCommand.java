package com.arcoymetal.arcobackend.identityaccess.internal.application.command;

import java.util.Objects;

public record LoginCommand(
        String email,
        String password
) {
    public LoginCommand {
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(password, "Password cannot be null");
    }
}
