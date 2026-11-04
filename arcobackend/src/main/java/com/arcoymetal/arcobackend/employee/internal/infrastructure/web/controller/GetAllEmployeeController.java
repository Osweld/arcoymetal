package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeDetails;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.GetAllEmployeeUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class GetAllEmployeeController {

    private final GetAllEmployeeUseCase getAllEmployeeUseCase;

    @GetMapping
    public ResponseEntity<List<EmployeeDetails>> getAllEmployees() {
        List<EmployeeDetails> employees = getAllEmployeeUseCase.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

}
