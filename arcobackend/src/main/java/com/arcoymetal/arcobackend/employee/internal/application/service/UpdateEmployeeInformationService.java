package com.arcoymetal.arcobackend.employee.internal.application.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.employee.internal.application.command.UpdateEmployeeInformationCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.UpdateEmployeeInformationUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeRepository;
import com.arcoymetal.arcobackend.employee.internal.domain.Employee;
import com.arcoymetal.arcobackend.employee.internal.domain.EmployeeId;
import com.arcoymetal.arcobackend.employee.internal.domain.JobTitle;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class UpdateEmployeeInformationService implements UpdateEmployeeInformationUseCase {
    
    private final EmployeeRepository employeeRepository;
    
    @Override
    @Transactional 
    public EmployeeId updateEmployeeInformation(UpdateEmployeeInformationCommand command) {

        Objects.requireNonNull(command, "UpdateEmployeeInformationCommand cannot be null");

        Employee employee = employeeRepository.findById(command.employeeId());
        updateEmployeeFields(employee, command);
        employeeRepository.save(employee);
        return employee.getEmployeeId();
    }


    private void updateEmployeeFields(Employee employee, UpdateEmployeeInformationCommand command) {
        employee.updateEmploymentInformation(
          new JobTitle(command.jobTitle()),
            command.contractType(),
            command.pensionFundInstitution()
        );
    }

}
