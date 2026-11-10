package com.arcoymetal.arcobackend.employee.internal.domain;

import java.util.Objects;

public class Employee {

    private final EmployeeId employeeId;

    // Información personal
    private Name name;
    private Email email;
    private PhoneNumber phoneNumber;
    private Gender gender;
    private BirthDate birthDate;
    private Address address;

    // Información laboral
    private JobTitle jobTitle;
    private BaseSalary baseSalary;
    private ContractType contractType;
    private PensionFundInstitution pensionFundInstitution;

    // Identificación
    private DUI dui;
    private NIT nit;
    private ISSS isss;
    private NUP nup;

    // Información contractual
    private HireDate hireDate;
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
                null, EmployeeStatus.ACTIVE);
    }

    public void updatePersonalInformation(
            Name name,
            Email email,
            PhoneNumber phoneNumber,
            Address address) {
        this.name = Objects.requireNonNull(name, "Name must not be null");
        this.email = Objects.requireNonNull(email, "Email must not be null");
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "Phone number must not be null");
        this.address = Objects.requireNonNull(address, "Address must not be null");
    }

    public void updateEmploymentInformation(
            JobTitle jobTitle,
            ContractType contractType,
            PensionFundInstitution pensionFundInstitution) {
        this.jobTitle = Objects.requireNonNull(jobTitle, "Job title must not be null");
        this.contractType = Objects.requireNonNull(contractType, "Contract type must not be null");
        this.pensionFundInstitution = Objects.requireNonNull(
                pensionFundInstitution, "Pension fund institution must not be null");
    }

    public void changeBaseSalary(BaseSalary baseSalary) {
        this.baseSalary = Objects.requireNonNull(baseSalary, "Base salary must not be null");
    }

    public void correctIdentificationInformation(
            DUI dui,
            NIT nit,
            ISSS isss,
            NUP nup) {
        this.dui = Objects.requireNonNull(dui, "DUI must not be null");
        this.nit = nit;
        this.isss = isss;
        this.nup = nup;
    }

    public void correctBirthDate(BirthDate birthDate) {
        this.birthDate = Objects.requireNonNull(birthDate, "Birth date must not be null");
    }

    public void correctHireDate(HireDate hireDate) {
        this.hireDate = Objects.requireNonNull(hireDate, "Hire date must not be null");
    }

    public static Employee reconstitute(EmployeeId employeeId, Name name, Email email, PhoneNumber phoneNumber,
            Gender gender,
            BirthDate birthDate, Address address, JobTitle jobTitle, BaseSalary baseSalary, ContractType contractType,
            DUI dui, NIT nit, ISSS isss, NUP nup, PensionFundInstitution pensionFundInstitution, HireDate hireDate,
            TerminationDate terminationDate, EmployeeStatus status) {

        return new Employee(employeeId, name, email, phoneNumber, gender, birthDate, address, jobTitle,
                baseSalary, contractType, dui, nit, isss, nup, pensionFundInstitution, hireDate,
                terminationDate, status);
    }

    public EmployeeId getEmployeeId() {
        return employeeId;
    }

    public Name getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public Gender getGender() {
        return gender;
    }

    public BirthDate getBirthDate() {
        return birthDate;
    }

    public Address getAddress() {
        return address;
    }

    public JobTitle getJobTitle() {
        return jobTitle;
    }

    public BaseSalary getBaseSalary() {
        return baseSalary;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public DUI getDui() {
        return dui;
    }

    public NIT getNit() {
        return nit;
    }

    public ISSS getIsss() {
        return isss;
    }

    public NUP getNup() {
        return nup;
    }

    public PensionFundInstitution getPensionFundInstitution() {
        return pensionFundInstitution;
    }

    public HireDate getHireDate() {
        return hireDate;
    }

    public TerminationDate getTerminationDate() {
        return terminationDate;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

}
