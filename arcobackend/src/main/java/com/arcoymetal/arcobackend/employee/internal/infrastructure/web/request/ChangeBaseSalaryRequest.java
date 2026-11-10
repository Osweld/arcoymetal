package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public record ChangeBaseSalaryRequest(
    @NotNull UUID employeeId,
    @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal newBaseSalary
) {

}
