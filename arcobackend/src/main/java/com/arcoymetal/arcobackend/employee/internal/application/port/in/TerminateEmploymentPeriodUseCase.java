package com.arcoymetal.arcobackend.employee.internal.application.port.in;

import com.arcoymetal.arcobackend.employee.internal.application.command.TerminateEmploymentPeriodCommand;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentId;

public interface TerminateEmploymentPeriodUseCase {

    EmploymentId terminateEmploymentPeriod(TerminateEmploymentPeriodCommand command);
}
