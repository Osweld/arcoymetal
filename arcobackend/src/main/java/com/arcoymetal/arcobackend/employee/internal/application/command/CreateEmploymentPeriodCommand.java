package com.arcoymetal.arcobackend.employee.internal.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.ContractType;

public record CreateEmploymentPeriodCommand(
        UUID employeeId,
        LocalDate startDate,
        String jobTitle,
        ContractType contractType,
        BigDecimal baseSalary
) {

}
