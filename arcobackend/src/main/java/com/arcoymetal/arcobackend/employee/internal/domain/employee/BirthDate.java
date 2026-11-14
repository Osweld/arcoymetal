package com.arcoymetal.arcobackend.employee.internal.domain.employee;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public record BirthDate(LocalDate value) {

   public BirthDate {
        Objects.requireNonNull(value, "Birth date cannot be null");

        if (value.isBefore(LocalDate.of(1920, 1, 1))) {
            throw new IllegalArgumentException("Birth date is unreasonably old");
        }
    }

    public int ageAt(LocalDate referenceDate) {
        Objects.requireNonNull(referenceDate, "Reference date cannot be null");
        if (referenceDate.isBefore(value)) {
            throw new IllegalArgumentException("Reference date cannot be before birth date");
        }
        return Period.between(value, referenceDate).getYears();
    }

}
