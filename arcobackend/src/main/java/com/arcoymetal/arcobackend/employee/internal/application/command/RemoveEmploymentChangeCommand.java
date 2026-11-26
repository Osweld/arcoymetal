package com.arcoymetal.arcobackend.employee.internal.application.command;

import java.util.Objects;
import java.util.UUID;

public record RemoveEmploymentChangeCommand(
        UUID employmentPeriodId,
        UUID employmentChangeId
) {
    public RemoveEmploymentChangeCommand {
        Objects.requireNonNull(employmentPeriodId, "Employment period ID cannot be null");
        Objects.requireNonNull(employmentChangeId, "Employment change ID cannot be null");
    }
}
