package com.arcoymetal.arcobackend.employee.internal.application.service;

import java.time.Clock;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.employee.internal.application.command.CreateEmployeeCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.CreateEmployeeUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeRepository;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Address;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.AddressLine;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.BirthDate;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.DUI;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.DistrictId;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Email;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.EmergencyContact;
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
public class CreateEmployeeService implements CreateEmployeeUseCase {

    private final EmployeeRepository employeeRepository;
    private final Clock clock;

    @Override
    @Transactional
    public EmployeeId createEmployee(CreateEmployeeCommand command) {

        Objects.requireNonNull(command, "CreateEmployeeCommand cannot be null");

        EmergencyContact emergencyContact = buildEmergencyContact(
                command.emergencyContactName(),
                command.emergencyContactPhone());

        EmployeeId employeeId = EmployeeId.generate();
        Employee employee = Employee.create(
                employeeId,
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
                emergencyContact,
                clock);

        if (employeeRepository.existsByEmail(employee.getEmail().value())) {
            throw new DuplicateEmployeeException("An employee with the provided email already exists.");
        }

        if (employeeRepository.existsByDui(employee.getDui().value())) {
            throw new DuplicateEmployeeException("An employee with the provided DUI already exists.");
        }

        if (employee.getNit() != null && employeeRepository.existsByNit(employee.getNit().value())) {
            throw new DuplicateEmployeeException("An employee with the provided NIT already exists.");
        }

        if (employee.getIsss() != null && employeeRepository.existsByIsss(employee.getIsss().value())) {
            throw new DuplicateEmployeeException("An employee with the provided ISSS already exists.");
        }

        if (employee.getNup() != null && employeeRepository.existsByNup(employee.getNup().value())) {
            throw new DuplicateEmployeeException("An employee with the provided NUP already exists.");
        }

        employeeRepository.save(employee);
        return employeeId;
    }

    private static EmergencyContact buildEmergencyContact(String name, String phone) {
        boolean hasName = hasText(name);
        boolean hasPhone = hasText(phone);
        if (!hasName && !hasPhone) {
            return null;
        }
        if (!hasName || !hasPhone) {
            throw new IllegalArgumentException(
                    "Both emergency contact name and phone number are required when providing an emergency contact.");
        }
        return new EmergencyContact(name, new PhoneNumber(phone));
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

}
