package com.arcoymetal.arcobackend.employee.internal.domain.exception;

import java.util.UUID;

public class EmploymentPeriodNotFoundException extends RuntimeException {

    public EmploymentPeriodNotFoundException(UUID employmentId) {
        super("Employment period not found with ID: " + employmentId);
    }
}
