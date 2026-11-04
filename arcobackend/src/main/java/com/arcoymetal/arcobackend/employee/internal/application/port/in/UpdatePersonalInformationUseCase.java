package com.arcoymetal.arcobackend.employee.internal.application.port.in;

import com.arcoymetal.arcobackend.employee.internal.application.command.UpdatePersonalInformationCommand;
import com.arcoymetal.arcobackend.employee.internal.domian.EmployeeId;

public interface UpdatePersonalInformationUseCase {

    EmployeeId updatePersonalInformation(UpdatePersonalInformationCommand command);

}
