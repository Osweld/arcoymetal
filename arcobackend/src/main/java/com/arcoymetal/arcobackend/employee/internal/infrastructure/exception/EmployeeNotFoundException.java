package com.arcoymetal.arcobackend.employee.internal.infrastructure.exception;

public class EmployeeNotFoundException extends RuntimeException {

     public EmployeeNotFoundException(String id) {
        super("Employee not found with ID: " + id);
    }

}
