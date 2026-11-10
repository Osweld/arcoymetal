package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request;

import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.domain.ContractType;
import com.arcoymetal.arcobackend.employee.internal.domain.PensionFundInstitution;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateEmployeeInformationRequest(
        @NotNull UUID employeeId,
        @NotBlank @Size(min = 1, max = 100) String jobTitle,
        @NotNull ContractType contractType,
        @NotNull PensionFundInstitution pensionFundInstitution) {

}
