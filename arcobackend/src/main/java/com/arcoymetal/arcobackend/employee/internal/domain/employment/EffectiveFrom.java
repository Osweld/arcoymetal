package com.arcoymetal.arcobackend.employee.internal.domain.employment;

import java.time.LocalDate;
import java.util.Objects;

public record EffectiveFrom(LocalDate value) {

    public EffectiveFrom {
        Objects.requireNonNull(value, "effectiveFrom cannot be null");

    }

}
