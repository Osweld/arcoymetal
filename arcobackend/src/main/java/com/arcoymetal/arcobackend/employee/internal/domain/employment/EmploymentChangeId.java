package com.arcoymetal.arcobackend.employee.internal.domain.employment;

import java.util.Objects;
import java.util.UUID;

public record EmploymentChangeId(UUID value) {

    public EmploymentChangeId {
        Objects.requireNonNull(value, "Employment change ID cannot be null");
    }

    public static EmploymentChangeId generate() {
        return new EmploymentChangeId(UUID.randomUUID());
    }

}
