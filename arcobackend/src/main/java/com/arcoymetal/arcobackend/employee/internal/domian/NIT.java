package com.arcoymetal.arcobackend.employee.internal.domian;

import java.util.Objects;
import java.util.regex.Pattern;

public record NIT(String value) {

     private static final Pattern NIT_PATTERN = Pattern.compile("^(?:\\d{14}|\\d{4}-\\d{6}-\\d{3}-\\d)$");

    public NIT {

        Objects.requireNonNull(value,"NIT cannot be null");

        if (!NIT_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("NIT must contain 14 digits, optionally separated by hyphens");
        }

        value = value.replace("-", "");
    }

}
