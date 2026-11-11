package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.projection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.Gender;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.PensionFundInstitution;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.ContractType;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmployeeStatus;

public interface EmployeeDetailsProjection {

        UUID getId();
        String getFirstName();
        String getLastName();
        String getEmail();
        String getPhoneNumber();
        Gender getGender();
        LocalDate getBirthDate();
        String getAddressLine();
        String getDistrictId();
        String getJobTitle();
        BigDecimal getBaseSalary();
        ContractType getContractType();
        String getDui();
        String getNit();
        String getIsss();
        String getNup();
        PensionFundInstitution getPensionFundInstitution();
        LocalDate getHireDate();
        LocalDate getTerminationDate();
        EmployeeStatus getStatus();
}
