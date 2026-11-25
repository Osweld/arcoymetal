package com.arcoymetal.arcobackend.employee.internal.domain.exception;

import java.util.UUID;

public class EmploymentPeriodNotActiveException extends RuntimeException {

    public EmploymentPeriodNotActiveException(UUID employmentId) {
        super("Employment period with ID " + employmentId + " is not active");
    }
}
