package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.employee.internal.application.command.UpdateEmployeeCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.UpdateEmployeeUseCase;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.mapper.EmployeeRequestMapper;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request.UpdateEmployeeRequest;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.response.UpdateEmployeeResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;


@RestController 
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor 
public class UpdateEmployeeController {

    private final UpdateEmployeeUseCase updateEmployeeUseCase; 
    private final EmployeeRequestMapper employeeRequestMapper;    


    @PutMapping("/{id}")
    public ResponseEntity<UpdateEmployeeResponse> updateEmployee(@PathVariable UUID id, @Valid @RequestBody UpdateEmployeeRequest entity) {
        UpdateEmployeeCommand command = employeeRequestMapper.toUpdateEmployeeCommand(id, entity);
        UpdateEmployeeResponse response = new UpdateEmployeeResponse(updateEmployeeUseCase.updateEmployee(command).value());
        return ResponseEntity.ok(response);
    }

}
