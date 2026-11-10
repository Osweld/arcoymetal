package com.arcoymetal.arcobackend.employee.internal.application.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.arcoymetal.arcobackend.employee.internal.application.command.CorrectIdentificationInformationCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.CorrectIdentificationInformationUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeRepository;
import com.arcoymetal.arcobackend.employee.internal.domain.DUI;
import com.arcoymetal.arcobackend.employee.internal.domain.Employee;
import com.arcoymetal.arcobackend.employee.internal.domain.EmployeeId;
import com.arcoymetal.arcobackend.employee.internal.domain.ISSS;
import com.arcoymetal.arcobackend.employee.internal.domain.NIT;
import com.arcoymetal.arcobackend.employee.internal.domain.NUP;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CorrectIdentificationInformationService implements CorrectIdentificationInformationUseCase {

    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeId correctIdentificationInformation(CorrectIdentificationInformationCommand command) {

        Objects.requireNonNull(command, "CorrectIdentificationInformationCommand cannot be null");

        Employee employee = employeeRepository.findById(command.employeeId());

        if (!command.dui().equals(employee.getDui().value()) && employeeRepository.existsByDui(command.dui())) {
            throw new IllegalArgumentException("DUI already exists");
        }

        updateEmployeeFields(employee, command);

        employeeRepository.save(employee);
        return employee.getEmployeeId();
    }

    private void updateEmployeeFields(Employee employee, CorrectIdentificationInformationCommand command) {
        employee.correctIdentificationInformation(
                new DUI(command.dui()),
                command.nit() != null ? new NIT(command.nit()) : null,
                command.isss() != null ? new ISSS(command.isss()) : null,
                command.nup() != null ? new NUP(command.nup()) : null);
    }

}
