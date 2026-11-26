package com.arcoymetal.arcobackend.employee.internal.domain.employee;

import java.util.Objects;

public record EmergencyContact(String name, PhoneNumber phoneNumber) {

    private static final int MAX_NAME_LENGTH = 150;

    public EmergencyContact {
        Objects.requireNonNull(name, "Emergency contact name cannot be null");
        Objects.requireNonNull(phoneNumber, "Emergency contact phone number cannot be null");

        name = name.strip();
        if (name.isBlank()) {
            throw new IllegalArgumentException("Emergency contact name cannot be blank");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Emergency contact name cannot exceed " + MAX_NAME_LENGTH + " characters");
        }
    }
}
