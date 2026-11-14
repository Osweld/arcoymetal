package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.ContractType;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateEmploymentPeriodRequest(
        @NotNull UUID employeeId,
        @NotNull LocalDate startDate,
        @NotBlank @Size(min = 1, max = 100) String jobTitle,
        @NotNull ContractType contractType,
        @NotNull @Digits(fraction = 2, integer = 10) BigDecimal baseSalary) {

}
