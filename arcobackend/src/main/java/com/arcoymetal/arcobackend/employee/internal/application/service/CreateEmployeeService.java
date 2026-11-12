package com.arcoymetal.arcobackend.employee.internal.application.service;

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
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Employee;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.EmployeeId;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.ISSS;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.NIT;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.NUP;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Name;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.PhoneNumber;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class CreateEmployeeService implements CreateEmployeeUseCase {

    private final EmployeeRepository employeeRepository;
    
    
    
    
    @Override
    @Transactional 
    public EmployeeId createEmployee(CreateEmployeeCommand command) {
        
        Objects.requireNonNull(command,"CreateEmployeeCommand cannot be null");

        if (employeeRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("An employee with the provided email already exists.");
        }

        if (employeeRepository.existsByDui(command.dui())) {
            throw new IllegalArgumentException("An employee with the provided DUI already exists.");
        }
        
        EmployeeId employeeId = EmployeeId.generate();
        Employee employee = toDomain(command, employeeId);
        employeeRepository.save(employee);
        return employeeId;
    }



    private static Employee toDomain(CreateEmployeeCommand command, EmployeeId employeeId) {

        return Employee.create(
           employeeId,
           new Name(command.firstName(), command.lastName()),
           new Email(command.email()),
           new PhoneNumber(command.phoneNumber()),
           command.gender(),
           new BirthDate(command.birthDate()),
           new Address(new AddressLine(command.addressLine()), new DistrictId(command.districtId())),
           new DUI(command.dui()),
           command.nit() != null ? new NIT(command.nit()) : null,
           command.isss() != null ? new ISSS(command.isss()) : null,
           command.nup() != null ? new NUP(command.nup()) : null,
           command.pensionFundInstitution()
        );
    }

}
