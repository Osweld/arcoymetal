package com.arcoymetal.arcobackend.employee.internal.domain.employee;

import java.util.Objects;

public record NUP(String value) {

    private static final String NUP_PATTERN = "^(?:\\d{9}|\\d{8}-\\d)$";

    public NUP {

        Objects.requireNonNull(value, "NUP cannot be null");

        if (!value.matches(NUP_PATTERN)) {
            throw new IllegalArgumentException("NUP must contain 9 digits, optionally separated by hyphens");
        }
        value = value.replace("-", "");
    }

}
