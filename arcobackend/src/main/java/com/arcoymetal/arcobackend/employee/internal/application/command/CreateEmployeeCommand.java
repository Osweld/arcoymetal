package com.arcoymetal.arcobackend.employee.internal.application.command;

import java.time.LocalDate;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.Gender;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.PensionFundInstitution;

public record CreateEmployeeCommand(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Gender gender,
        LocalDate birthDate,
        String addressLine,
        String districtId,
        String dui,
        String nit,
        String isss,
        String nup,
        PensionFundInstitution pensionFundInstitution
) {

}
