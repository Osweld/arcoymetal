package com.arcoymetal.arcobackend.employee.internal.domian;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

public record HireDate(LocalDate date) {

    public HireDate {
       
        Objects.requireNonNull(date, "Hire date cannot be null");

        if (date.isAfter(LocalDate.now(ZoneId.of("America/El_Salvador")))) {
            throw new IllegalArgumentException("Hire date cannot be in the future");
        }
    }

}
