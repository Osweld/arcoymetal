package com.arcoymetal.arcobackend.employee.internal.domain.exception;

import java.util.UUID;

public class ActiveEmploymentPeriodAlreadyExistsException extends RuntimeException {

    public ActiveEmploymentPeriodAlreadyExistsException(UUID employeeId) {
        super("Employee with ID " + employeeId + " already has an active employment period");
    }
}
