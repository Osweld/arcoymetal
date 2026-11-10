package com.arcoymetal.arcobackend.employee.internal.domain;

import java.util.Objects;

public record AddressLine(String value) {

    private static final int MAX_LENGTH = 150;

    public AddressLine {
        
        Objects.requireNonNull(value, "AddressLine value cannot be null");
        
        value = value.strip();

        if(value.isBlank()) {
            throw new IllegalArgumentException("AddressLine value cannot be blank");
        }
        if(value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("AddressLine cannot exceed " + MAX_LENGTH + " characters");
        }
    }

}
