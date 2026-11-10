package com.arcoymetal.arcobackend.employee.internal.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record ChangeBaseSalaryCommand(
    UUID employeeId,
    BigDecimal newBaseSalary
) {

}
