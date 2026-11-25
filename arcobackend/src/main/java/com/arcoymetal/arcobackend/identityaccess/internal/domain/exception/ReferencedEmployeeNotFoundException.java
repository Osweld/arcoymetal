package com.arcoymetal.arcobackend.identityaccess.internal.domain.exception;

import java.util.UUID;

public class ReferencedEmployeeNotFoundException extends RuntimeException {

    public ReferencedEmployeeNotFoundException(UUID employeeId) {
        super("Employee with employeeId " + employeeId + " does not exist.");
    }
}
