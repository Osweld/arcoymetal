package com.arcoymetal.arcobackend.identityaccess.internal.domain.vo;

import java.util.Objects;
import java.util.regex.Pattern;

public record Email(String value) {

    private static final int MAX_LENGTH = 254;

    private static final Pattern EMAIL_REGEX = Pattern.compile(
        "^[A-Za-z0-9_%+-]+(?:\\.[A-Za-z0-9_%+-]+)*@"
        + "(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+"
        + "[A-Za-z]{2,}$");

    public Email {
        Objects.requireNonNull(value, "Email cannot be null");

        value = value.strip();

        if (value.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Email cannot exceed " + MAX_LENGTH + " characters");
        }

        if (!EMAIL_REGEX.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

}
