package com.arcoymetal.arcobackend.employee.internal.application.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.arcoymetal.arcobackend.employee.internal.application.command.UpdatePersonalInformationCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.UpdatePersonalInformationUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeRepository;
import com.arcoymetal.arcobackend.employee.internal.domian.Address;
import com.arcoymetal.arcobackend.employee.internal.domian.AddressLine;
import com.arcoymetal.arcobackend.employee.internal.domian.DistrictId;
import com.arcoymetal.arcobackend.employee.internal.domian.Email;
import com.arcoymetal.arcobackend.employee.internal.domian.Employee;
import com.arcoymetal.arcobackend.employee.internal.domian.EmployeeId;
import com.arcoymetal.arcobackend.employee.internal.domian.Name;
import com.arcoymetal.arcobackend.employee.internal.domian.PhoneNumber;

@Service
public class UpdatePersonalInformationService implements UpdatePersonalInformationUseCase {

    private final EmployeeRepository employeeRepository;

    public UpdatePersonalInformationService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeId updatePersonalInformation(UpdatePersonalInformationCommand command) {

        Objects.requireNonNull(command, "UpdatePersonalInformationCommand cannot be null");

        Employee employee = employeeRepository.findById(command.employeeId());

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
                new DistrictId(command.districtId())
            ));
    }

}
