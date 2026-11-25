package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request;

import java.time.LocalDate;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.TerminationReason;

import jakarta.validation.constraints.NotNull;

public record TerminateEmploymentPeriodRequest(
        @NotNull LocalDate endDate,
        @NotNull TerminationReason terminationReason
) {
}
