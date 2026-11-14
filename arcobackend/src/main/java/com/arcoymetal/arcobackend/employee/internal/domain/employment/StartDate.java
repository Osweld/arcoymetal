package com.arcoymetal.arcobackend.employee.internal.domain.employment;

import java.time.LocalDate;
import java.util.Objects;

public record StartDate(LocalDate value) {

    public StartDate {
       
        Objects.requireNonNull(value, "Start date cannot be null");

        if (value.isBefore(LocalDate.of(1950, 1, 1))) {
            throw new IllegalArgumentException("Start date cannot be before 1950");
        }
    }

}
