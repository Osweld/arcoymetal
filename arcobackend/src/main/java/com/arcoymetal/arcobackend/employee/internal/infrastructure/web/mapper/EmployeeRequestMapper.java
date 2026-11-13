package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.arcoymetal.arcobackend.employee.internal.application.command.CreateEmployeeCommand;
import com.arcoymetal.arcobackend.employee.internal.application.command.UpdateEmployeeCommand;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request.CreateEmployeeRequest;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request.UpdateEmployeeRequest;

@Component 
public class EmployeeRequestMapper {

    public CreateEmployeeCommand toCreateEmployeeCommand(CreateEmployeeRequest request) {
        return new CreateEmployeeCommand(
          request.firstName(),
          request.lastName(),
          request.email(),
          request.phoneNumber(),
          request.gender(),
          request.birthDate(),
          request.addressLine(),
          request.districtId(),
          request.dui(),
          request.nit(),
          request.isss(),
          request.nup(),
          request.pensionFundInstitution()
        );
    }

    public UpdateEmployeeCommand toUpdateEmployeeCommand(UUID employeeId, UpdateEmployeeRequest request) {
        return new UpdateEmployeeCommand(
          employeeId,
          request.firstName(),
          request.lastName(),
          request.email(),
          request.phoneNumber(),
          request.gender(),
          request.birthDate(),
          request.addressLine(),
          request.districtId(),
          request.dui(),
          request.nit(),
          request.isss(),
          request.nup(),
          request.pensionFundInstitution()
        );
    }

}
