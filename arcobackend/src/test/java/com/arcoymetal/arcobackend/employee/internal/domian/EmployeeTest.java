package com.arcoymetal.arcobackend.employee.internal.domian;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.Address;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.AddressLine;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.BirthDate;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.DUI;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.DistrictId;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Email;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Employee;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.EmployeeId;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Gender;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.ISSS;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.NIT;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.NUP;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Name;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.PensionFundInstitution;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.PhoneNumber;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.BaseSalary;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.ContractType;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmployeeStatus;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.StartDate;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.JobTitle;

public class EmployeeTest {

        final EmployeeId employeeId = new EmployeeId(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"));
        Name name = new Name("John", "Doe");
        Email email = new Email("john.doe@example.com");
        PhoneNumber phoneNumber = new PhoneNumber("70123485");
        Gender gender = Gender.MALE;
        BirthDate birthDate = new BirthDate(LocalDate.of(1990, 1, 1));
        Address address = new Address(
            new AddressLine("Colonia primavera"),
            new DistrictId("D001")
        );
        JobTitle jobTitle = new JobTitle("Software Engineer");
        BaseSalary baseSalary = new BaseSalary(new BigDecimal("50000.00"));
        ContractType contractType = ContractType.FULL_TIME;
        DUI dui = new DUI("12345678-9");
        NIT nit = new NIT("12341234561231");
        ISSS isss = new ISSS("123456789");
        NUP nup = new NUP("123456789");
        StartDate hireDate = new StartDate(LocalDate.of(2020, 1, 1));
        PensionFundInstitution pensionFundInstitution = PensionFundInstitution.AFP_CONFIA;  

    @Test
    void shouldCreateEmployeeWithActiveStatus() {
        Employee employee = Employee.create(
                employeeId,
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
        assertThat(employee.getEmployeeId()).isEqualTo(employeeId);
        assertThat(employee.getName()).isEqualTo(name);
        assertThat(employee.getEmail()).isEqualTo(email);
        assertThat(employee.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(employee.getGender()).isEqualTo(gender);
        assertThat(employee.getBirthDate()).isEqualTo(birthDate);
        assertThat(employee.getAddress()).isEqualTo(address);
        assertThat(employee.getJobTitle()).isEqualTo(jobTitle);
        assertThat(employee.getBaseSalary()).isEqualTo(baseSalary);
        assertThat(employee.getContractType()).isEqualTo(contractType);
        assertThat(employee.getDui()).isEqualTo(dui);
        assertThat(employee.getNit()).isEqualTo(nit);
        assertThat(employee.getIsss()).isEqualTo(isss);
        assertThat(employee.getNup()).isEqualTo(nup);
        assertThat(employee.getPensionFundInstitution()).isEqualTo(pensionFundInstitution);
        assertThat(employee.getHireDate()).isEqualTo(hireDate); 
    }


    @Test
    void shouldCreateEmployeeWithActiveStatusWithNullValues() {
        Employee employee = Employee.create(
                employeeId,
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
                null,// nit
                null, // isss
                null, // nup
                pensionFundInstitution,
                hireDate);
        assertThat(employee.getEmployeeId()).isEqualTo(employeeId);
        assertThat(employee.getName()).isEqualTo(name);
        assertThat(employee.getEmail()).isEqualTo(email);
        assertThat(employee.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(employee.getGender()).isEqualTo(gender);
        assertThat(employee.getBirthDate()).isEqualTo(birthDate);
        assertThat(employee.getAddress()).isEqualTo(address);
        assertThat(employee.getJobTitle()).isEqualTo(jobTitle);
        assertThat(employee.getBaseSalary()).isEqualTo(baseSalary);
        assertThat(employee.getContractType()).isEqualTo(contractType);
        assertThat(employee.getDui()).isEqualTo(dui);
        assertThat(employee.getNit()).isNull();
        assertThat(employee.getIsss()).isNull();
        assertThat(employee.getNup()).isNull();
        assertThat(employee.getPensionFundInstitution()).isEqualTo(pensionFundInstitution);
        assertThat(employee.getHireDate()).isEqualTo(hireDate); 
    }




    @Test
    void shouldReconstituteTerminatedEmployee() {

        Employee employee = Employee.reconstitute(
                employeeId,
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
                hireDate,
            null, // terminationDate
                EmployeeStatus.TERMINATED);

        assertThat(employee.getEmployeeId()).isEqualTo(employeeId);
        assertThat(employee.getName()).isEqualTo(name);
        assertThat(employee.getEmail()).isEqualTo(email);
        assertThat(employee.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(employee.getGender()).isEqualTo(gender);
        assertThat(employee.getBirthDate()).isEqualTo(birthDate);
        assertThat(employee.getAddress()).isEqualTo(address);
        assertThat(employee.getJobTitle()).isEqualTo(jobTitle);
        assertThat(employee.getBaseSalary()).isEqualTo(baseSalary);
        assertThat(employee.getContractType()).isEqualTo(contractType);
        assertThat(employee.getDui()).isEqualTo(dui);
        assertThat(employee.getNit()).isEqualTo(nit);
        assertThat(employee.getIsss()).isEqualTo(isss);
        assertThat(employee.getNup()).isEqualTo(nup);
        assertThat(employee.getPensionFundInstitution()).isEqualTo(pensionFundInstitution);
        assertThat(employee.getHireDate()).isEqualTo(hireDate); 
        assertThat(employee.getTerminationDate()).isNull();
        assertThat(employee.getStatus()).isEqualTo(EmployeeStatus.TERMINATED);

    }
    
    //TODO: Add more tests for Employee class, including edge cases and validation scenarios.

}
