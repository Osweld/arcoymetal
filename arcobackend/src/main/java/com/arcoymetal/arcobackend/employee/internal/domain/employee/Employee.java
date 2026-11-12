package com.arcoymetal.arcobackend.employee.internal.domain.employee;

import java.util.Objects;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.BaseSalary;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.ContractType;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentStatus;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.StartDate;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.JobTitle;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EndDate;

public class Employee {

    private final EmployeeId employeeId;
    private Name name;
    private Email email;
    private PhoneNumber phoneNumber;
    private Gender gender;
    private BirthDate birthDate;
    private Address address;
    private DUI dui;
    private NIT nit;
    private ISSS isss;
    private NUP nup;

    private Employee(
            EmployeeId employeeId,
            Name name,
            Email email,
            PhoneNumber phoneNumber,
            Gender gender,
            BirthDate birthDate,
            Address address,
            DUI dui,
            NIT nit,
            ISSS isss,
            NUP nup
    ) {
        this.employeeId = Objects.requireNonNull(employeeId, "Employee ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "Phone number cannot be null");
        this.gender = Objects.requireNonNull(gender, "Gender cannot be null");
        this.birthDate = Objects.requireNonNull(birthDate, "Birth date cannot be null");
        this.address = Objects.requireNonNull(address, "Address cannot be null");
        this.dui = Objects.requireNonNull(dui, "DUI cannot be null");
        this.nit = Objects.requireNonNull(nit, "NIT cannot be null");
        this.isss = Objects.requireNonNull(isss, "ISSS cannot be null");
        this.nup = Objects.requireNonNull(nup, "NUP cannot be null");
    }



}
