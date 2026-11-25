package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.arcoymetal.arcobackend.employee.internal.application.command.CreateEmploymentPeriodCommand;
import com.arcoymetal.arcobackend.employee.internal.application.command.RegisterEmploymentChangeCommand;
import com.arcoymetal.arcobackend.employee.internal.application.command.TerminateEmploymentPeriodCommand;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request.CreateEmploymentPeriodRequest;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request.RegisterEmploymentChangeRequest;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request.TerminateEmploymentPeriodRequest;

@Component
public class EmploymentPeriodRequestMapper {

    public CreateEmploymentPeriodCommand toCommand(CreateEmploymentPeriodRequest request) {
        return new CreateEmploymentPeriodCommand(
                request.employeeId(),
                request.startDate(),
                request.jobTitle(),
                request.contractType(),
                request.baseSalary()
        );
    }

    public RegisterEmploymentChangeCommand toRegisterChangeCommand(
            UUID employmentPeriodId,
            RegisterEmploymentChangeRequest request
    ) {
        return new RegisterEmploymentChangeCommand(
                employmentPeriodId,
                request.jobTitle(),
                request.contractType(),
                request.baseSalary(),
                request.effectiveFrom()
        );
    }

    public TerminateEmploymentPeriodCommand toTerminateCommand(
            UUID employmentPeriodId,
            TerminateEmploymentPeriodRequest request
    ) {
        return new TerminateEmploymentPeriodCommand(
                employmentPeriodId,
                request.endDate(),
                request.terminationReason()
        );
    }
}
