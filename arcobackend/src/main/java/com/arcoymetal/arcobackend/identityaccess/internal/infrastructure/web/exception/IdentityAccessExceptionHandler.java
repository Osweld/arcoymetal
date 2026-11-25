package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.exception;

import java.time.Instant;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.DuplicateUserException;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.ReferencedEmployeeNotFoundException;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.UserAlreadyHasRoleException;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.UserDoesNotHaveRoleException;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.UserMustHaveAtLeastOneRoleException;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.UserNotFoundException;
import com.arcoymetal.arcobackend.shared.infrastructure.web.exception.ApiErrorResponse;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class IdentityAccessExceptionHandler {

    @ExceptionHandler({ UserNotFoundException.class, ReferencedEmployeeNotFoundException.class })
    public ResponseEntity<ApiErrorResponse> handleNotFound(RuntimeException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({ DuplicateUserException.class, UserAlreadyHasRoleException.class })
    public ResponseEntity<ApiErrorResponse> handleConflict(RuntimeException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler({ UserMustHaveAtLeastOneRoleException.class, UserDoesNotHaveRoleException.class })
    public ResponseEntity<ApiErrorResponse> handleDomainBadRequest(RuntimeException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                null
        );
        return ResponseEntity.badRequest().body(response);
    }
}
