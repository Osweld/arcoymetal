package com.arcoymetal.arcobackend.employee.internal.domain.exception;

import java.util.UUID;

public class EmploymentChangeNotFoundException extends RuntimeException {

    public EmploymentChangeNotFoundException(UUID changeId) {
        super("Employment change not found with ID: " + changeId);
    }
}
