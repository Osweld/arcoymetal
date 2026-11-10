package com.arcoymetal.arcobackend.employee.internal.domain;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

public record BirthDate(LocalDate value) {

    public BirthDate {
        Objects.requireNonNull(value, "BirthDate value cannot be null");

        if(value.isAfter(LocalDate.now(ZoneId.of("America/El_Salvador")))) {
            throw new IllegalArgumentException("BirthDate cannot be in the future");
        }
    }

}
