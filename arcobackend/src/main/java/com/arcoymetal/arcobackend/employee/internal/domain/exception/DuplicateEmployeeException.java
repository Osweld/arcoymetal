package com.arcoymetal.arcobackend.employee.internal.domain.exception;

public class DuplicateEmployeeException extends RuntimeException {

    public DuplicateEmployeeException(String message) {
        super(message);
    }
}
