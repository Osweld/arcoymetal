package com.arcoymetal.arcobackend.employee.internal.domain.employment;

import java.time.LocalDate;
import java.util.Objects;

public record EndDate(LocalDate value) {

    public EndDate {
        Objects.requireNonNull(value, "EndDate value cannot be null");
    }

    public boolean isBefore(StartDate startDate) {
        return value.isBefore(startDate.value());
    }

}
