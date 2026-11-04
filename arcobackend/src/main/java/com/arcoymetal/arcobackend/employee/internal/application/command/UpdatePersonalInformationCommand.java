package com.arcoymetal.arcobackend.employee.internal.application.command;

import java.util.UUID;

public record UpdatePersonalInformationCommand(
    UUID employeeId,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String addressLine,
    String districtId
) {

}
