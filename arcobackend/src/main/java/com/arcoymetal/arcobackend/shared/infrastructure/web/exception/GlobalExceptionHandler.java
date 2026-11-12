package com.arcoymetal.arcobackend.shared.infrastructure.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

//     @ExceptionHandler(MethodArgumentNotValidException.class)
//     public ResponseEntity<ApiErrorResponse> handleValidation(
//             MethodArgumentNotValidException exception
//     ) {
//         Map<String, String> errors = exception.getBindingResult()
//                 .getFieldErrors()
//                 .stream()
//                 .collect(java.util.stream.Collectors.toMap(
//                         error -> error.getField(),
//                         error -> error.getDefaultMessage() != null
//                                 ? error.getDefaultMessage()
//                                 : "Invalid value",
//                         (first, second) -> first
//                 ));

//         ApiErrorResponse response = new ApiErrorResponse(
//                 Instant.now(),
//                 HttpStatus.BAD_REQUEST.value(),
//                 "Validation failed",
//                 errors
//         );

//         log.error(exception.getMessage());

//         return ResponseEntity.badRequest().body(response);
//     }

//     @ExceptionHandler(IllegalArgumentException.class)
//     public ResponseEntity<ApiErrorResponse> handleUnexpected(
//             IllegalArgumentException exception
//     ) {
//         ApiErrorResponse response = new ApiErrorResponse(
//                 Instant.now(),
//                 HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                 exception.getMessage(),
//                 null
//         );

//         log.error(exception.getMessage());

//         return ResponseEntity.internalServerError().body(response);
//     }

//     @ExceptionHandler(NullPointerException.class)
//     public ResponseEntity<ApiErrorResponse> handleUnexpected(
//             NullPointerException exception
//     ) {
//         ApiErrorResponse response = new ApiErrorResponse(
//                 Instant.now(),
//                 HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                 exception.getMessage(),
//                 null
//         );

//         log.error(exception.getMessage());

//         return ResponseEntity.internalServerError().body(response);
//     }

//     @ExceptionHandler(Exception.class)
//     public ResponseEntity<ApiErrorResponse> handleUnexpected(
//             Exception exception
//     ) {
//         ApiErrorResponse response = new ApiErrorResponse(
//                 Instant.now(),
//                 HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                 "An unexpected error occurred",
//                 null
//         );

//         log.error(exception.getMessage());

//         return ResponseEntity.internalServerError().body(response);
//     }
}



