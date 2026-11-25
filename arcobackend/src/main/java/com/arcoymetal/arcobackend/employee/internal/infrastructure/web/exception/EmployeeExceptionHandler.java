package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.exception;

import java.time.Instant;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.arcoymetal.arcobackend.employee.internal.domain.exception.ActiveEmploymentPeriodAlreadyExistsException;
import com.arcoymetal.arcobackend.employee.internal.domain.exception.DuplicateEmployeeException;
import com.arcoymetal.arcobackend.employee.internal.domain.exception.DuplicateEmploymentChangeDateException;
import com.arcoymetal.arcobackend.employee.internal.domain.exception.EmployeeNotFoundException;
import com.arcoymetal.arcobackend.employee.internal.domain.exception.EmploymentPeriodNotActiveException;
import com.arcoymetal.arcobackend.employee.internal.domain.exception.EmploymentPeriodNotFoundException;
import com.arcoymetal.arcobackend.employee.internal.domain.exception.OverlappingEmploymentPeriodException;
import com.arcoymetal.arcobackend.shared.infrastructure.web.exception.ApiErrorResponse;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EmployeeExceptionHandler {

    @ExceptionHandler({ EmployeeNotFoundException.class, EmploymentPeriodNotFoundException.class })
    public ResponseEntity<ApiErrorResponse> handleNotFound(RuntimeException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({
            DuplicateEmployeeException.class,
            ActiveEmploymentPeriodAlreadyExistsException.class,
            EmploymentPeriodNotActiveException.class,
            DuplicateEmploymentChangeDateException.class,
            OverlappingEmploymentPeriodException.class
    })
    public ResponseEntity<ApiErrorResponse> handleConflict(RuntimeException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
