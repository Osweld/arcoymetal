package com.arcoymetal.arcobackend.employee.internal.application.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.Gender;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.PensionFundInstitution;

public record EmployeeDetails(
    UUID employeeId,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    Gender gender,
    LocalDate birthDate,
    String addressLine,
    String districtId,
    String dui,
    String nit,
    String isss,
    String nup,
    PensionFundInstitution pensionFundInstitution,
    Instant createdAt
) {

}
