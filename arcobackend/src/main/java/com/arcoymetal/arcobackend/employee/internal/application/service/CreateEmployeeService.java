package com.arcoymetal.arcobackend.employee.internal.application.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.employee.internal.application.command.CreateEmployeeCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.CreateEmployeeUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeRepository;
import com.arcoymetal.arcobackend.employee.internal.domian.Address;
import com.arcoymetal.arcobackend.employee.internal.domian.AddressLine;
import com.arcoymetal.arcobackend.employee.internal.domian.BaseSalary;
import com.arcoymetal.arcobackend.employee.internal.domian.BirthDate;
import com.arcoymetal.arcobackend.employee.internal.domian.ContractType;
import com.arcoymetal.arcobackend.employee.internal.domian.DUI;
import com.arcoymetal.arcobackend.employee.internal.domian.DistrictId;
import com.arcoymetal.arcobackend.employee.internal.domian.Email;
import com.arcoymetal.arcobackend.employee.internal.domian.Employee;
import com.arcoymetal.arcobackend.employee.internal.domian.EmployeeId;
import com.arcoymetal.arcobackend.employee.internal.domian.Gender;
import com.arcoymetal.arcobackend.employee.internal.domian.HireDate;
import com.arcoymetal.arcobackend.employee.internal.domian.ISSS;
import com.arcoymetal.arcobackend.employee.internal.domian.JobTitle;
import com.arcoymetal.arcobackend.employee.internal.domian.NIT;
import com.arcoymetal.arcobackend.employee.internal.domian.NUP;
import com.arcoymetal.arcobackend.employee.internal.domian.Name;
import com.arcoymetal.arcobackend.employee.internal.domian.PensionFundInstitution;
import com.arcoymetal.arcobackend.employee.internal.domian.PhoneNumber;

@Service 
public class CreateEmployeeService implements CreateEmployeeUseCase {

    private final EmployeeRepository employeeRepository;

    public CreateEmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional()
    public EmployeeId createEmployee(CreateEmployeeCommand command) {

        Objects.requireNonNull(command, "CreateEmployeeCommand cannot be null");

        if (employeeRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("An employee with the provided email already exists.");
        }

        if (employeeRepository.existsByDui(command.dui())) {
            throw new IllegalArgumentException("An employee with the provided DUI already exists.");
        }


        EmployeeId employeeId = EmployeeId.generate();
        Name name = new Name(command.firstName(), command.lastName());
        Email email = new Email(command.email());
        NIT nit = command.nit() != null ? new NIT(command.nit()) : null;
        ISSS isss = command.isss() != null ? new ISSS(command.isss()) : null;
        NUP nup = command.nup() != null ? new NUP(command.nup()) : null;
        PhoneNumber phoneNumber = new PhoneNumber(command.phoneNumber());
        Gender gender = command.gender();
        BirthDate birthDate = new BirthDate(command.birthDate());
        Address address = new Address(
                new AddressLine(command.addressLine()),
                new DistrictId(command.districtId()));
        JobTitle jobTitle = new JobTitle(command.jobTitle());
        BaseSalary baseSalary = new BaseSalary(command.baseSalary());
        ContractType contractType = command.contractType();
        DUI dui = new DUI(command.dui());
        PensionFundInstitution pensionFundInstitution = command.pensionFundInstitution();
        HireDate hireDate = new HireDate(command.hireDate());

        Employee employee = Employee.create(employeeId,
                name,
                email,
                phoneNumber,
                gender,
                birthDate,
                address,
                jobTitle,
                baseSalary,
                contractType,
                dui,
                nit,
                isss,
                nup,
                pensionFundInstitution,
                hireDate);

        employeeRepository.save(employee);

        return employeeId;

    }

}
