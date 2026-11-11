package com.arcoymetal.arcobackend.employee.internal.domain.employee;

import java.util.Objects;

public record ISSS(String value) {

    private static final String ISSS_PATTERN = "^(?:\\d{9}|\\d{8}-\\d)$";

    public ISSS {

        Objects.requireNonNull(value, "ISSS cannot be null");

        if (!value.matches(ISSS_PATTERN)) {
            throw new IllegalArgumentException("ISSS must contain 9 digits, optionally separated by a hyphen");
        }
        value = value.replace("-", "");
    }
}
