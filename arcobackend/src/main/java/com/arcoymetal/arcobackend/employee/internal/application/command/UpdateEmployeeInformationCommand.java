package com.arcoymetal.arcobackend.employee.internal.application.command;

import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.domain.ContractType;
import com.arcoymetal.arcobackend.employee.internal.domain.PensionFundInstitution;

public record UpdateEmployeeInformationCommand(
    UUID employeeId,
    String jobTitle,
    ContractType contractType,
    PensionFundInstitution pensionFundInstitution
) {

}
