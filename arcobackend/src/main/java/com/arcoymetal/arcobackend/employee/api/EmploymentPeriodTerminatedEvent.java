package com.arcoymetal.arcobackend.employee.api;

import java.time.LocalDate;
import java.util.UUID;

public record EmploymentPeriodTerminatedEvent(
        UUID employeeId,
        UUID employmentPeriodId,
        LocalDate endDate
) {
}
