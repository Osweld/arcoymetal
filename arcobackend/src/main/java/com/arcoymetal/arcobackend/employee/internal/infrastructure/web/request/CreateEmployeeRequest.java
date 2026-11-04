package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.arcoymetal.arcobackend.employee.internal.domian.ContractType;
import com.arcoymetal.arcobackend.employee.internal.domian.Gender;
import com.arcoymetal.arcobackend.employee.internal.domian.PensionFundInstitution;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateEmployeeRequest(
        @NotBlank @Size(min = 1, max = 100) String firstName,
        @NotBlank @Size(min = 1, max = 100) String lastName,
        @NotBlank @Email @Size(min = 1, max = 254) String email,
        @NotBlank @Size(min = 8, max = 8) String phoneNumber,
        @NotNull Gender gender,
        @NotNull @Past LocalDate birthDate,
        @NotBlank @Size(min = 1, max = 150) String addressLine,
        @NotBlank String districtId,
        @NotBlank @Size(min = 1, max = 100) String jobTitle,
        @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal baseSalary,
        @NotNull ContractType contractType,
        @NotBlank @Pattern(regexp = "^(?:\\d{9}|\\d{8}-\\d)$") String dui,
        @Pattern(regexp = "^(?:\\d{14}|\\d{4}-\\d{6}-\\d{3}-\\d)$") String nit,
        @Pattern(regexp = "^(?:\\d{9}|\\d{8}-\\d)$") String isss,
        @Pattern(regexp = "^(?:\\d{9}|\\d{8}-\\d)$") String nup,
        @NotNull PensionFundInstitution pensionFundInstitution,
        @NotNull @PastOrPresent LocalDate hireDate) {
}
