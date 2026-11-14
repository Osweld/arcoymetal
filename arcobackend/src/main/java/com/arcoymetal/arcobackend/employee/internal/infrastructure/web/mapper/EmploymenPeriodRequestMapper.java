package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.arcoymetal.arcobackend.employee.internal.application.command.CreateEmploymentPeriodCommand;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request.CreateEmploymentPeriodRequest;

@Component
public class EmploymenPeriodRequestMapper {

    public CreateEmploymentPeriodCommand toCommand(CreateEmploymentPeriodRequest request) {
        return new CreateEmploymentPeriodCommand(
                request.employeeId(),
                request.startDate(),
                request.jobTitle(),
                request.contractType(),
                request.baseSalary()
        );
    }

}
