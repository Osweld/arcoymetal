package com.arcoymetal.arcobackend.employee.internal.application.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.employee.internal.application.command.UpdatePersonalInformationCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.UpdatePersonalInformationUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeRepository;
import com.arcoymetal.arcobackend.employee.internal.domain.Address;
import com.arcoymetal.arcobackend.employee.internal.domain.AddressLine;
import com.arcoymetal.arcobackend.employee.internal.domain.BirthDate;
import com.arcoymetal.arcobackend.employee.internal.domain.DistrictId;
import com.arcoymetal.arcobackend.employee.internal.domain.Email;
import com.arcoymetal.arcobackend.employee.internal.domain.Employee;
import com.arcoymetal.arcobackend.employee.internal.domain.EmployeeId;
import com.arcoymetal.arcobackend.employee.internal.domain.Name;
import com.arcoymetal.arcobackend.employee.internal.domain.PhoneNumber;

@Service
public class UpdatePersonalInformationService implements UpdatePersonalInformationUseCase {

    private final EmployeeRepository employeeRepository;

    public UpdatePersonalInformationService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public EmployeeId updatePersonalInformation(UpdatePersonalInformationCommand command) {

        Objects.requireNonNull(command, "UpdatePersonalInformationCommand cannot be null");

        Employee employee = employeeRepository.findById(command.employeeId());

        if (!command.email().equals(employee.getEmail().value()) && employeeRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        

        updateEmployeeFields(employee, command);

        employeeRepository.update(employee);

        return employee.getEmployeeId();
    }

    private void updateEmployeeFields(Employee employee, UpdatePersonalInformationCommand command) {
        employee.updatePersonalInformation(
                new Name(command.firstName(), command.lastName()),
                new Email(command.email()),
                new PhoneNumber(command.phoneNumber()),
                new Address(
                        new AddressLine(command.addressLine()),
                        new DistrictId(command.districtId())),
                new BirthDate(command.birthDate()));
    }

}
