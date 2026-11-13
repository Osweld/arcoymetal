package com.arcoymetal.arcobackend.employee.internal.application.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class UpdadeEmployeeService implements UpdateEmployeeUseCase {
    
    private final EmployeeRepository employeeRepository;
    
    @Override
    @Transactional 
    public EmployeeId updateEmployee(UpdateEmployeeCommand command) {

        Objects.requireNonNull(command,"UpdateEmployeeCommand cannot be null");

        Employee existingEmployee = employeeRepository.findById(command.employeeId());

        Employee updatedEmployee = toDomain(command, existingEmployee.getEmployeeId());


        if (!existingEmployee.getEmail().equals(updatedEmployee.getEmail()) && employeeRepository.existsByEmail(updatedEmployee.getEmail().value())) {
            throw new IllegalArgumentException("An employee with the provided email already exists.");
        }

        if (!existingEmployee.getDui().equals(updatedEmployee.getDui()) && employeeRepository.existsByDui(updatedEmployee.getDui().value())) {
            throw new IllegalArgumentException("An employee with the provided DUI already exists.");
        }



        employeeRepository.save(updatedEmployee);

        return updatedEmployee.getEmployeeId();
       

    }



     private static Employee toDomain(UpdateEmployeeCommand command, EmployeeId employeeId) {

        return Employee.update(
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
