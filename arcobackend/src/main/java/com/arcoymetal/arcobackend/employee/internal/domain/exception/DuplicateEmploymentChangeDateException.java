package com.arcoymetal.arcobackend.employee.internal.domain.exception;

import java.time.LocalDate;

public class DuplicateEmploymentChangeDateException extends RuntimeException {

    public DuplicateEmploymentChangeDateException(LocalDate effectiveFrom) {
        super("An employment change already exists for effectiveFrom date: " + effectiveFrom);
    }
}
