package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.employee.internal.application.command.CreateEmployeeCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.CreateEmployeeUseCase;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.mapper.EmployeeRequestMapper;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request.CreateEmployeeRequest;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.response.CreateEmployeeResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping ("/api/v1/employees")
@RequiredArgsConstructor 
public class CreateEmployeeController {

    private final EmployeeRequestMapper employeeRequestMapper;
    private final CreateEmployeeUseCase createEmployeeUseCase;


    @PostMapping
    public ResponseEntity<CreateEmployeeResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
       CreateEmployeeCommand command = employeeRequestMapper.toCreateEmployeeCommand(request);
       CreateEmployeeResponse response = new CreateEmployeeResponse(createEmployeeUseCase.createEmployee(command).value());
       return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
    

}
