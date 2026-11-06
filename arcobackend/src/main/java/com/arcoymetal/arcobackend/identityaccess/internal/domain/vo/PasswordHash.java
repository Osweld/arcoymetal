package com.arcoymetal.arcobackend.identityaccess.internal.domain.vo;

import java.util.Objects;

public record PasswordHash(String value) {

    private static final int MIN_LENGTH = 60;
    private static final int MAX_LENGTH = 100;

    public PasswordHash {

        Objects.requireNonNull(value,"Password hash cannot be null");

        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Password hash must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters long");
        }
    }


}
