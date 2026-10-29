package com.arcoymetal.arcobackend.employee.internal.domian;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

public record TerminationDate(LocalDate value) {

    public TerminationDate {

        Objects.requireNonNull(value, "TerminationDate value cannot be null");

        if (value.isBefore(LocalDate.now(ZoneId.of("America/El_Salvador")))) {
            throw new IllegalArgumentException("TerminationDate cannot be in the past");
        }

    }

}
