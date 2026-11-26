package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.projection;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.Gender;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.PensionFundInstitution;

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
    String getDui();
    String getNit();
    String getIsss();
    String getNup();
    PensionFundInstitution getPensionFundInstitution();
    String getEmergencyContactName();
    String getEmergencyContactPhone();
    Instant getCreatedAt();

}