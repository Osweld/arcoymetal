package com.arcoymetal.arcobackend.employee.internal.application.service;

import java.time.Clock;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.employee.internal.application.command.UpdateEmployeeCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.UpdateEmployeeUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeRepository;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Address;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.AddressLine;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.BirthDate;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.DUI;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.DistrictId;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Email;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Employee;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.EmployeeId;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.ISSS;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.NIT;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.NUP;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Name;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.PhoneNumber;
import com.arcoymetal.arcobackend.employee.internal.domain.exception.DuplicateEmployeeException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateEmployeeService implements UpdateEmployeeUseCase {

    private final EmployeeRepository employeeRepository;
    private final Clock clock;

    @Override
    @Transactional
    public EmployeeId updateEmployee(UpdateEmployeeCommand command) {

        Objects.requireNonNull(command, "UpdateEmployeeCommand cannot be null");

        Employee existingEmployee = employeeRepository.findById(command.employeeId());

        Employee updatedEmployee = Employee.update(
                existingEmployee.getEmployeeId(),
                new Name(command.firstName(), command.lastName()),
                new Email(command.email()),
                new PhoneNumber(command.phoneNumber()),
                command.gender(),
                new BirthDate(command.birthDate()),
                new Address(new AddressLine(command.addressLine()), new DistrictId(command.districtId())),
                new DUI(command.dui()),
                hasText(command.nit()) ? new NIT(command.nit()) : null,
                hasText(command.isss()) ? new ISSS(command.isss()) : null,
                hasText(command.nup()) ? new NUP(command.nup()) : null,
                command.pensionFundInstitution(),
                clock);

        if (!updatedEmployee.getEmail().equals(existingEmployee.getEmail())
                && employeeRepository.existsByEmail(updatedEmployee.getEmail().value())) {
            throw new DuplicateEmployeeException("An employee with the provided email already exists.");
        }

        if (!updatedEmployee.getDui().equals(existingEmployee.getDui())
                && employeeRepository.existsByDui(updatedEmployee.getDui().value())) {
            throw new DuplicateEmployeeException("An employee with the provided DUI already exists.");
        }

        if (updatedEmployee.getNit() != null
                && !Objects.equals(updatedEmployee.getNit(), existingEmployee.getNit())
                && employeeRepository.existsByNit(updatedEmployee.getNit().value())) {
            throw new DuplicateEmployeeException("An employee with the provided NIT already exists.");
        }

        if (updatedEmployee.getIsss() != null
                && !Objects.equals(updatedEmployee.getIsss(), existingEmployee.getIsss())
                && employeeRepository.existsByIsss(updatedEmployee.getIsss().value())) {
            throw new DuplicateEmployeeException("An employee with the provided ISSS already exists.");
        }

        if (updatedEmployee.getNup() != null
                && !Objects.equals(updatedEmployee.getNup(), existingEmployee.getNup())
                && employeeRepository.existsByNup(updatedEmployee.getNup().value())) {
            throw new DuplicateEmployeeException("An employee with the provided NUP already exists.");
        }

        employeeRepository.save(updatedEmployee);

        return updatedEmployee.getEmployeeId();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
