package com.arcoymetal.arcobackend.employee.internal.application.port.in;

import com.arcoymetal.arcobackend.employee.internal.application.command.CreateEmploymentPeriodCommand;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentId;

public interface CreateEmploymentPeriodUseCase {

    EmploymentId createEmploymentPeriod(CreateEmploymentPeriodCommand command);

}
