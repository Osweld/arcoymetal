package com.arcoymetal.arcobackend.employee.internal.application.port.in;

import com.arcoymetal.arcobackend.employee.internal.application.command.CorrectIdentificationInformationCommand;
import com.arcoymetal.arcobackend.employee.internal.domain.EmployeeId;

public interface CorrectIdentificationInformationUseCase {

    EmployeeId correctIdentificationInformation(CorrectIdentificationInformationCommand command);

}
