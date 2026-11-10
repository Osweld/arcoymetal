package com.arcoymetal.arcobackend.employee.internal.application.command;

import java.util.UUID;

public record CorrectIdentificationInformationCommand(
    UUID employeeId,
    String dui,
    String nit,
    String isss,
    String nup
) {

}
