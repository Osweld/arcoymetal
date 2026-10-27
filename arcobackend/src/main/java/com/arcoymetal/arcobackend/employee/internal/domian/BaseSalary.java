package com.arcoymetal.arcobackend.employee.internal.domian;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record BaseSalary(BigDecimal value) {
    
    public BaseSalary {
        Objects.requireNonNull(value, "Base salary value cannot be null");

        if(value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Base salary cannot be negative");
        }

        value = value.setScale(2, RoundingMode.UNNECESSARY);
    }

}
