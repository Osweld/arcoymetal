package com.arcoymetal.arcobackend.employee.internal.application.port.in;

import com.arcoymetal.arcobackend.employee.internal.application.command.UpdateEmployeeCommand;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.EmployeeId;

public interface UpdateEmployeeUseCase {

    EmployeeId updateEmployee(UpdateEmployeeCommand command);

}
