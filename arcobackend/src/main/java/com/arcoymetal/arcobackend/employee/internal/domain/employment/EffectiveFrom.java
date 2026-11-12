package com.arcoymetal.arcobackend.employee.internal.domain.employment;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

public record EffectiveFrom(LocalDate value) {

    public EffectiveFrom {
        Objects.requireNonNull(value, "Hire date cannot be null");

        if (value.isAfter(LocalDate.now(ZoneId.of("America/El_Salvador")))) {
            throw new IllegalArgumentException("Hire date cannot be in the future");
        }
    }

}
