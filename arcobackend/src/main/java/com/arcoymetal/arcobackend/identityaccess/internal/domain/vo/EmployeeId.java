package com.arcoymetal.arcobackend.identityaccess.internal.domain.vo;

import java.util.Objects;
import java.util.UUID;

public record EmployeeId(UUID value) {

    public EmployeeId {
        Objects.requireNonNull(value, "EmployeeId value cannot be null");
    }

    public static EmployeeId generate() {
        return new EmployeeId(UUID.randomUUID());
    }

}
