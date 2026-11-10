package com.arcoymetal.arcobackend.employee.internal.application.port.in;

import com.arcoymetal.arcobackend.employee.internal.application.command.UpdateEmployeeInformationCommand;
import com.arcoymetal.arcobackend.employee.internal.domain.EmployeeId;

public interface UpdateEmployeeInformationUseCase {

    EmployeeId updateEmployeeInformation(UpdateEmployeeInformationCommand command);

}
