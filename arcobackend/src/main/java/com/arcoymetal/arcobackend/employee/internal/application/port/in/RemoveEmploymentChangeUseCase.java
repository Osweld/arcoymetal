package com.arcoymetal.arcobackend.employee.internal.application.port.in;

import com.arcoymetal.arcobackend.employee.internal.application.command.RemoveEmploymentChangeCommand;

public interface RemoveEmploymentChangeUseCase {

    void removeChange(RemoveEmploymentChangeCommand command);

}
