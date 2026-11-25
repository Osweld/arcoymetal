package com.arcoymetal.arcobackend.employee.internal.application.query;

import java.util.Objects;
import java.util.UUID;

public record GetEmployeeEmploymentHistoryQuery(UUID employeeId) {

    public GetEmployeeEmploymentHistoryQuery {
        Objects.requireNonNull(employeeId, "employeeId cannot be null");
    }
}
