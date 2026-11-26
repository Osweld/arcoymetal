package com.arcoymetal.arcobackend.employee.internal.domain.exception;

public class CannotDeleteInitialEmploymentChangeException extends RuntimeException {

    public CannotDeleteInitialEmploymentChangeException() {
        super("Cannot delete the initial employment change of an employment period");
    }
}
