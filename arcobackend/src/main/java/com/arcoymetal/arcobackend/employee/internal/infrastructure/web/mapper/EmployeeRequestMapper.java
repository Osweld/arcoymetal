package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.mapper;

import org.springframework.stereotype.Component;

import com.arcoymetal.arcobackend.employee.internal.application.command.CreateEmployeeCommand;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request.CreateEmployeeRequest;

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

}
