package com.arcoymetal.arcobackend.employee.internal.application.port.in;

import com.arcoymetal.arcobackend.employee.internal.application.command.CreateEmployeeCommand;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.EmployeeId;

public interface CreateEmployeeUseCase {

    EmployeeId createEmployee(CreateEmployeeCommand command);

}
