package com.arcoymetal.arcobackend.employee.internal.domain;

import java.util.Objects;

public record JobTitle(String value) {

    public JobTitle {
        Objects.requireNonNull(value, "JobTitle value cannot be null");
        value = value.strip();
        if (value.isBlank()) {
            throw new IllegalArgumentException("JobTitle value cannot be blank");

        }
    }

}
