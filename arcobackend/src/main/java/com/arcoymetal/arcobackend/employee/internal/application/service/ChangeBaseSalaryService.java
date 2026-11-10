package com.arcoymetal.arcobackend.employee.internal.application.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.arcoymetal.arcobackend.employee.internal.application.command.ChangeBaseSalaryCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.ChangeBaseSalaryUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeRepository;
import com.arcoymetal.arcobackend.employee.internal.domain.BaseSalary;
import com.arcoymetal.arcobackend.employee.internal.domain.Employee;
import com.arcoymetal.arcobackend.employee.internal.domain.EmployeeId;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class ChangeBaseSalaryService implements ChangeBaseSalaryUseCase {

    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeId changeBaseSalary(ChangeBaseSalaryCommand command) {

        Objects.requireNonNull(command, "ChangeBaseSalaryCommand cannot be null");
        
       Employee employee = employeeRepository.findById(command.employeeId());

        employee.changeBaseSalary(new BaseSalary(command.newBaseSalary()));
        employeeRepository.update(employee);

        return employee.getEmployeeId();
    }
    

}
