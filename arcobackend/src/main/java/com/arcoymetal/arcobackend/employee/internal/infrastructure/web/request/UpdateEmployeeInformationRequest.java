package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.arcoymetal.arcobackend.employee.internal.domain.ContractType;
import com.arcoymetal.arcobackend.employee.internal.domain.PensionFundInstitution;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record UpdateEmployeeInformationRequest(
                @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal baseSalary,
                @NotBlank @Size(min = 1, max = 100) String jobTitle,
                @NotNull ContractType contractType,
                @NotNull PensionFundInstitution pensionFundInstitution,
                @NotNull @PastOrPresent LocalDate hireDate) {

}
