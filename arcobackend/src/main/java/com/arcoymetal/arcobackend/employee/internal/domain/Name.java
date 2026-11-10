package com.arcoymetal.arcobackend.employee.internal.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public record Name(String firstName, String lastName) {

    private static final int MAX_NAME_LENGTH = 100;
    private static final int MIN_NAME_LENGTH = 1;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}\\p{M} .'-]+$");

    public Name {
        firstName = normalize(firstName, "firstName");
        lastName = normalize(lastName, "lastName");

        validate(firstName, "firstName");
        validate(lastName, "lastName");
    }

    public String fullName() {
        return firstName + " " + lastName;
    }

    private static String normalize(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " cannot be null");
        return value.strip();
    }

    private static void validate(String value, String fieldName) {
        if (value.length() < MIN_NAME_LENGTH || value.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    fieldName + " must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters");
        }
        if (!NAME_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(fieldName + " contains invalid characters");
        }
    }

}
