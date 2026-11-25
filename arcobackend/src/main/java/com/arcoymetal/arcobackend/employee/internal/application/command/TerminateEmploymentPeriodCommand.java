package com.arcoymetal.arcobackend.employee.internal.application.command;

import java.time.LocalDate;
import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.TerminationReason;

public record TerminateEmploymentPeriodCommand(
        UUID employmentPeriodId,
        LocalDate endDate,
        TerminationReason terminationReason
) {
}
