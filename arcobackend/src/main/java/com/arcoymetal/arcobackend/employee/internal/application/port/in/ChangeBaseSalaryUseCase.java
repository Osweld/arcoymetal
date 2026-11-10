package com.arcoymetal.arcobackend.employee.internal.application.port.in;

import com.arcoymetal.arcobackend.employee.internal.application.command.ChangeBaseSalaryCommand;
import com.arcoymetal.arcobackend.employee.internal.domain.EmployeeId;

public interface ChangeBaseSalaryUseCase {

    EmployeeId changeBaseSalary(ChangeBaseSalaryCommand command);

}
