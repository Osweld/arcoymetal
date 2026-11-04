package com.arcoymetal.arcobackend.employee.internal.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.domian.ContractType;
import com.arcoymetal.arcobackend.employee.internal.domian.EmployeeStatus;
import com.arcoymetal.arcobackend.employee.internal.domian.Gender;
import com.arcoymetal.arcobackend.employee.internal.domian.PensionFundInstitution;

public record EmployeeDetails(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Gender gender,
        LocalDate birthDate,
        String addressLine,
        String districtId,
        String jobTitle,
        BigDecimal baseSalary,
        ContractType contractType,
        String dui,
        String nit,
        String isss,
        String nup,
        PensionFundInstitution pensionFundInstitution,
        LocalDate hireDate,
        LocalDate terminationDate,
        EmployeeStatus status
) {
}
