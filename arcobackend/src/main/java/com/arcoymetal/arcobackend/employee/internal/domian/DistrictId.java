package com.arcoymetal.arcobackend.employee.internal.domian;

import java.util.Objects;

public record DistrictId(String value) {

    public DistrictId {
        Objects.requireNonNull(value, "DistrictId value cannot be null");
        value = value.strip();
        if (value.isBlank()) {
            throw new IllegalArgumentException("DistrictId value cannot be blank");
        }
    }

}
