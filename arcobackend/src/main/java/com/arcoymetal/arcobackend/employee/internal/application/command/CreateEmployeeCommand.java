package com.arcoymetal.arcobackend.employee.internal.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.arcoymetal.arcobackend.employee.internal.domain.ContractType;
import com.arcoymetal.arcobackend.employee.internal.domain.Gender;
import com.arcoymetal.arcobackend.employee.internal.domain.PensionFundInstitution;

public record CreateEmployeeCommand(
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
        LocalDate hireDate) {

}
