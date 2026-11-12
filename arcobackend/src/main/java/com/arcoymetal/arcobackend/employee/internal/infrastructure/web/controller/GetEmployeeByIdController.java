package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeDetails;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.GetEmployeeByIdUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.query.GetEmployeeByIdQuery;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor 
public class GetEmployeeByIdController {

    private final GetEmployeeByIdUseCase getEmployeeByIdUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDetails> getEmployeeById(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(getEmployeeByIdUseCase.getEmployeeById(new GetEmployeeByIdQuery(id)), HttpStatus.OK);
    }
    

}
