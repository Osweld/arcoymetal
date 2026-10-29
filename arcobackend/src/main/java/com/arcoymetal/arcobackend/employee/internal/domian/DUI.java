package com.arcoymetal.arcobackend.employee.internal.domian;

import java.util.Objects;
import java.util.regex.Pattern;

public record DUI(String value) {

    private static final Pattern DUI_PATTERN = Pattern.compile("^(?:\\d{9}|\\d{8}-\\d)$");

    public DUI {

        Objects.requireNonNull(value,"DUI cannot be null");

        value = value.strip();

        if (!DUI_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("DUI must contain 9 digits, optionally separated by a hyphen");
        }

        value = value.replace("-", "");
        
    }

}
