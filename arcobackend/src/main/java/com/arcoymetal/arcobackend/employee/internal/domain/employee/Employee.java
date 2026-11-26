package com.arcoymetal.arcobackend.employee.internal.domain.employee;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;

public class Employee {

    private final EmployeeId employeeId;
    private final Name name;
    private final Email email;
    private final PhoneNumber phoneNumber;
    private final Gender gender;
    private final BirthDate birthDate;
    private final Address address;
    private final DUI dui;
    private final NIT nit;
    private final ISSS isss;
    private final NUP nup;
    private final PensionFundInstitution pensionFundInstitution;
    private final EmergencyContact emergencyContact;

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
            NUP nup,
            PensionFundInstitution pensionFundInstitution,
            EmergencyContact emergencyContact) {
        this.employeeId = Objects.requireNonNull(employeeId, "Employee ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "Phone number cannot be null");
        this.gender = Objects.requireNonNull(gender, "Gender cannot be null");
        this.birthDate = Objects.requireNonNull(birthDate, "Birth date cannot be null");
        this.address = Objects.requireNonNull(address, "Address cannot be null");
        this.dui = Objects.requireNonNull(dui, "DUI cannot be null");
        this.nit = nit;
        this.isss = isss;
        this.nup = nup;
        this.pensionFundInstitution = Objects.requireNonNull(pensionFundInstitution,
                "Pension Fund Institution cannot be null");
        this.emergencyContact = emergencyContact;
    }

    public static Employee create(
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
            NUP nup,
            PensionFundInstitution pensionFundInstitution,
            Clock clock) {
        return create(employeeId, name, email, phoneNumber, gender, birthDate, address, dui, nit, isss, nup,
                pensionFundInstitution, null, clock);
    }

    public static Employee create(
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
            NUP nup,
            PensionFundInstitution pensionFundInstitution,
            EmergencyContact emergencyContact,
            Clock clock) {

        Objects.requireNonNull(birthDate, "Birth date cannot be null");
        Objects.requireNonNull(clock, "Clock cannot be null");

        LocalDate today = LocalDate.now(clock);

        if (birthDate.value().isAfter(today)) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }

        if (birthDate.ageAt(today) < 18) {
            throw new IllegalArgumentException("Employee must be at least 18 years old");
        }

        return new Employee(employeeId, name, email, phoneNumber, gender, birthDate, address, dui, nit, isss, nup,
                pensionFundInstitution, emergencyContact);
    }

    public static Employee update(
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
            NUP nup,
            PensionFundInstitution pensionFundInstitution,
            Clock clock) {
        return update(employeeId, name, email, phoneNumber, gender, birthDate, address, dui, nit, isss, nup,
                pensionFundInstitution, null, clock);
    }

    public static Employee update(
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
            NUP nup,
            PensionFundInstitution pensionFundInstitution,
            EmergencyContact emergencyContact,
            Clock clock) {
        Objects.requireNonNull(birthDate, "Birth date cannot be null");
        Objects.requireNonNull(clock, "Clock cannot be null");

        LocalDate today = LocalDate.now(clock);
        if (birthDate.value().isAfter(today)) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }
        if (birthDate.ageAt(today) < 18) {
            throw new IllegalArgumentException("Employee must be at least 18 years old");
        }
        return new Employee(employeeId, name, email, phoneNumber, gender, birthDate, address, dui, nit, isss, nup,
                pensionFundInstitution, emergencyContact);
    }

    public static Employee reconstitute(
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
            NUP nup,
            PensionFundInstitution pensionFundInstitution) {
        return reconstitute(employeeId, name, email, phoneNumber, gender, birthDate, address, dui, nit, isss, nup,
                pensionFundInstitution, null);
    }

    public static Employee reconstitute(
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
            NUP nup,
            PensionFundInstitution pensionFundInstitution,
            EmergencyContact emergencyContact) {
        return new Employee(employeeId, name, email, phoneNumber, gender, birthDate, address, dui, nit, isss, nup,
                pensionFundInstitution, emergencyContact);
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

    public EmergencyContact getEmergencyContact() {
        return emergencyContact;
    }

}
