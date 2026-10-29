package com.arcoymetal.arcobackend.employee.internal.domian;

import java.util.Objects;
import java.util.regex.Pattern;

public record PhoneNumber(String value) {

    private static final Pattern PHONE_NUMBER_REGEX = Pattern.compile("^\\d{8}$");

    public PhoneNumber {
        Objects.requireNonNull(value, "Phone number cannot be null");
        value = value.strip();
        if (!PHONE_NUMBER_REGEX.matcher(value).matches()) {
            throw new IllegalArgumentException("Phone number must be 8 digits");

        }

    }
}