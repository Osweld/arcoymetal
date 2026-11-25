package com.arcoymetal.arcobackend.employee.internal.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.ContractType;

public record EmploymentChangeDetails(
        UUID employmentChangeId,
        String jobTitle,
        ContractType contractType,
        BigDecimal baseSalary,
        LocalDate effectiveFrom,
        Instant createdAt
) {
}
