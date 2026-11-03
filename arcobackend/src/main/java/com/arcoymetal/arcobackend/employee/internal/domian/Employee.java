package com.arcoymetal.arcobackend.employee.internal.domian;

import java.util.Objects;

public class Employee {

    private final EmployeeId employeeId;
    private Name name;
    private Email email;
    private PhoneNumber phoneNumber;
    private final Gender gender;
    private final BirthDate birthDate;
    private Address address;
    private JobTitle jobTitle;
    private BaseSalary baseSalary;
    private ContractType contractType;
    private final DUI dui;
    private NIT nit;
    private ISSS isss;
    private NUP nup;
    private final PensionFundInstitution pensionFundInstitution;
    private final HireDate hireDate;
    private TerminationDate terminationDate;
    private EmployeeStatus status;


    private Employee(EmployeeId employeeId, Name name, Email email, PhoneNumber phoneNumber, Gender gender,
            BirthDate birthDate, Address address, JobTitle jobTitle, BaseSalary baseSalary, ContractType contractType,
            DUI dui, NIT nit, ISSS isss, NUP nup, PensionFundInstitution pensionFundInstitution, HireDate hireDate,
            TerminationDate terminationDate, EmployeeStatus status) {
        this.employeeId = Objects.requireNonNull(employeeId, "Employee ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "Phone number cannot be null");
        this.gender = Objects.requireNonNull(gender, "Gender cannot be null");
        this.birthDate = Objects.requireNonNull(birthDate, "Birth date cannot be null");
        this.address = Objects.requireNonNull(address, "Address cannot be null");
        this.jobTitle = Objects.requireNonNull(jobTitle, "Job title cannot be null");
        this.baseSalary = Objects.requireNonNull(baseSalary, "Base salary cannot be null");
        this.contractType = Objects.requireNonNull(contractType, "Contract type cannot be null");
        this.dui = Objects.requireNonNull(dui, "DUI cannot be null");
        this.nit = nit;
        this.isss = isss;
        this.nup = nup;
        this.pensionFundInstitution = Objects.requireNonNull(pensionFundInstitution,
                "Pension fund institution cannot be null");
        this.hireDate = Objects.requireNonNull(hireDate, "Hire date cannot be null");
        this.terminationDate = terminationDate;
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }

    public static Employee create(EmployeeId employeeId, Name name, Email email, PhoneNumber phoneNumber, Gender gender,
            BirthDate birthDate, Address address, JobTitle jobTitle, BaseSalary baseSalary, ContractType contractType,
            DUI dui, NIT nit, ISSS isss, NUP nup, PensionFundInstitution pensionFundInstitution, HireDate hireDate) {

        

        return new Employee(employeeId, name, email, phoneNumber, gender, birthDate, address, jobTitle,
                baseSalary, contractType, dui, nit, isss, nup, pensionFundInstitution, hireDate,
                null,EmployeeStatus.ACTIVE);
    }

    public static Employee reconstitute(EmployeeId employeeId, Name name, Email email, PhoneNumber phoneNumber, Gender gender,
            BirthDate birthDate, Address address, JobTitle jobTitle, BaseSalary baseSalary, ContractType contractType,
            DUI dui, NIT nit, ISSS isss, NUP nup, PensionFundInstitution pensionFundInstitution, HireDate hireDate,
            TerminationDate terminationDate, EmployeeStatus status) {

        return new Employee(employeeId, name, email, phoneNumber, gender, birthDate, address, jobTitle,
                baseSalary, contractType, dui, nit, isss, nup, pensionFundInstitution, hireDate,
                terminationDate, status);
    }

}
