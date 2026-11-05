package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeDetails;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.GetEmployeeByIdUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.query.GetEmployeeByIdQuery;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class GetEmployeeByIdController {

    private final GetEmployeeByIdUseCase getEmployeeByIdUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDetails> getEmployeeById(@PathVariable UUID id) {
        EmployeeDetails employeeDetails = getEmployeeByIdUseCase.getEmployeeById(new GetEmployeeByIdQuery(id));
        return ResponseEntity.ok(employeeDetails);
    }

}
