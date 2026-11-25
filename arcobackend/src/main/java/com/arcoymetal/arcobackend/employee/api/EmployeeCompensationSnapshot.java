package com.arcoymetal.arcobackend.employee.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EmployeeCompensationSnapshot(
        UUID employeeId,
        String fullName,
        String dui,
        String nit,
        String isss,
        String nup,
        String pensionFundInstitution,
        UUID employmentPeriodId,
        LocalDate periodStartDate,
        String jobTitle,
        String contractType,
        BigDecimal baseSalary,
        LocalDate effectiveFrom
) {
}
