package com.arcoymetal.arcobackend.employee.internal.domain.exception;

import java.time.LocalDate;
import java.util.UUID;

public class OverlappingEmploymentPeriodException extends RuntimeException {

    public OverlappingEmploymentPeriodException(UUID employeeId, LocalDate startDate) {
        super("Start date " + startDate + " overlaps with a previous employment period for employee " + employeeId);
    }
}
