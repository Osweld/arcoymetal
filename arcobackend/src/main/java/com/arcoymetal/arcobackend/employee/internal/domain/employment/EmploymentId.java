package com.arcoymetal.arcobackend.employee.internal.domain.employment;

import java.util.Objects;
import java.util.UUID;

public record EmploymentId(UUID value) {

    public EmploymentId{

        Objects.requireNonNull(value,"EmploymentId value cannot be null");
    }

}
