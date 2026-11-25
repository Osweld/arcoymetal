package com.arcoymetal.arcobackend.employee.internal.application.port.in;

import com.arcoymetal.arcobackend.employee.internal.application.command.RegisterEmploymentChangeCommand;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentChangeId;

public interface RegisterEmploymentChangeUseCase {

    EmploymentChangeId registerChange(RegisterEmploymentChangeCommand command);
}
