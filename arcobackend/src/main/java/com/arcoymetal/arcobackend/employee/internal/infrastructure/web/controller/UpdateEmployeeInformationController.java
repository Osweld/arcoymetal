package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.employee.internal.application.command.UpdateEmployeeInformationCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.UpdateEmployeeInformationUseCase;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.mapper.EmployeeRequestMapper;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request.UpdateEmployeeInformationRequest;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.response.UpdateEmployeeInformationResponse;

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
public class UpdateEmployeeInformationController {

    private final UpdateEmployeeInformationUseCase updateEmployeeInformationUseCase;
    private final EmployeeRequestMapper employeeRequestMapper;

    @PutMapping("/{id}/employee-information")
    public ResponseEntity<UpdateEmployeeInformationResponse> updateEmployeeInformation(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEmployeeInformationRequest entity) {
        UpdateEmployeeInformationCommand command = employeeRequestMapper.toUpdateEmployeeInformationCommand(entity, id);
        UpdateEmployeeInformationResponse response = new UpdateEmployeeInformationResponse(updateEmployeeInformationUseCase.updateEmployeeInformation(command).value());

        return ResponseEntity.ok(response);
    }

}
