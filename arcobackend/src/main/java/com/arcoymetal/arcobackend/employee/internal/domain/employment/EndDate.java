package com.arcoymetal.arcobackend.employee.internal.domain.employment;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

public record EndDate(LocalDate value) {

    public EndDate {

        Objects.requireNonNull(value, "TerminationDate value cannot be null");

        if (value.isBefore(LocalDate.now(ZoneId.of("America/El_Salvador")))) {
            throw new IllegalArgumentException("TerminationDate cannot be in the past");
        }

    }

}
