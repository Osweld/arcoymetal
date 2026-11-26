package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.UserSummary;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.GetUserByEmployeeIdUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class GetUserByEmployeeIdController {

    private final GetUserByEmployeeIdUseCase getUserByEmployeeIdUseCase;

    @GetMapping("/by-employee/{employeeId}")
    public ResponseEntity<UserSummary> getUserByEmployeeId(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(getUserByEmployeeIdUseCase.getUserByEmployeeId(employeeId));
    }
}
