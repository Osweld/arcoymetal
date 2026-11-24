package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request;

import java.time.LocalDate;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.Gender;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.PensionFundInstitution;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateEmployeeRequest(
        @NotBlank @Size(min = 1, max = 100) String firstName,
        @NotBlank @Size(min = 1, max = 100) String lastName,
        @NotBlank @Size(min = 1, max = 254) @Email String email,
        @NotBlank @Pattern(regexp = "^\\d{8}$") String phoneNumber,
        @NotNull Gender gender,
        @NotNull @Past LocalDate birthDate,
        @NotBlank @Size(min = 1, max = 150) String addressLine,
        @NotBlank @Size(min = 1, max = 10) String districtId,
        @NotBlank @Pattern(regexp = "^(?:\\d{9}|\\d{8}-\\d)$") String dui,
        @Pattern(regexp = "^(?:\\d{14}|\\d{4}-\\d{6}-\\d{3}-\\d)$") String nit,
        @Pattern(regexp = "^(?:\\d{9}|\\d{8}-\\d)$") String isss,
        @Pattern(regexp = "^(?:\\d{9}|\\d{8}-\\d)$") String nup,
        @NotNull PensionFundInstitution pensionFundInstitution) {

}
