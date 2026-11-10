package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.arcoymetal.arcobackend.employee.internal.application.command.CorrectIdentificationInformationCommand;
import com.arcoymetal.arcobackend.employee.internal.application.command.CreateEmployeeCommand;
import com.arcoymetal.arcobackend.employee.internal.application.command.UpdateEmployeeInformationCommand;
import com.arcoymetal.arcobackend.employee.internal.application.command.UpdatePersonalInformationCommand;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request.CorrectIdentificationInformationRequest;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request.CreateEmployeeRequest;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request.UpdateEmployeeInformationRequest;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request.UpdatePersonalInformationRequest;

@Component 
public class EmployeeRequestMapper {

    public CreateEmployeeCommand toCommand(CreateEmployeeRequest request) {
        return new CreateEmployeeCommand(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phoneNumber(),
                request.gender(),
                request.birthDate(),
                request.addressLine(),
                request.districtId(),
                request.jobTitle(),
                request.baseSalary(),
                request.contractType(),
                request.dui(),
                request.nit() != null ? request.nit() : null,
                request.isss() != null ? request.isss() : null,
                request.nup() != null ? request.nup() : null,
                request.pensionFundInstitution(),
                request.hireDate()
        );
    }

    public UpdatePersonalInformationCommand toUpdatePersonalInformationCommand(UpdatePersonalInformationRequest request, String id) {
        return new UpdatePersonalInformationCommand(
                UUID.fromString(id),
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phoneNumber(),
                request.addressLine(),
                request.districtId(),
                request.birthDate()
        );
    }

    public UpdateEmployeeInformationCommand toUpdateEmployeeInformationCommand(UpdateEmployeeInformationRequest request, UUID id) {
        return new UpdateEmployeeInformationCommand(
                id,
                request.baseSalary(),
                request.jobTitle(),
                request.contractType(),
                request.pensionFundInstitution(),
                request.hireDate()
        );
    }

    public CorrectIdentificationInformationCommand toCorrectIdentificationInformationCommand(CorrectIdentificationInformationRequest request, UUID id) {
        return new CorrectIdentificationInformationCommand(
                id,
                request.dui(),
                request.nit() != null ? request.nit() : null,
                request.isss() != null ? request.isss() : null,
                request.nup() != null ? request.nup() : null
        );
    }


}
