package com.arcoymetal.arcobackend.employee.internal.application.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentStatus;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.TerminationReason;

public record EmploymentPeriodDetails(
        UUID employmentPeriodId,
        UUID employeeId,
        LocalDate startDate,
        LocalDate endDate,
        EmploymentStatus status,
        TerminationReason terminationReason,
        List<EmploymentChangeDetails> changes
) {
}
