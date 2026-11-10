package com.arcoymetal.arcobackend.employee.internal.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.domain.ContractType;
import com.arcoymetal.arcobackend.employee.internal.domain.PensionFundInstitution;

public record UpdateEmployeeInformationCommand(
    UUID employeeId,
    BigDecimal baseSalary,
    String jobTitle,
    ContractType contractType,
    PensionFundInstitution pensionFundInstitution,
    LocalDate hireDate
) {

}
