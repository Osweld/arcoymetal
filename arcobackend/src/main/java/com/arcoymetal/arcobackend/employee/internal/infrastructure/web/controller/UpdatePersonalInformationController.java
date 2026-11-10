package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.employee.internal.application.command.UpdatePersonalInformationCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.UpdatePersonalInformationUseCase;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.mapper.EmployeeRequestMapper;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request.UpdatePersonalInformationRequest;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.response.UpdatePersonalInformationResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class UpdatePersonalInformationController {

    private final UpdatePersonalInformationUseCase updatePersonalInformationUseCase;
    private final EmployeeRequestMapper employeeRequestMapper;

    @PutMapping("/{id}/personal-information")
    public ResponseEntity<UpdatePersonalInformationResponse> putMethodName(
            @PathVariable String id,
            @Valid @RequestBody UpdatePersonalInformationRequest request) {
        UpdatePersonalInformationCommand command = employeeRequestMapper.toUpdatePersonalInformationCommand(request,
                id);
        UpdatePersonalInformationResponse response = new UpdatePersonalInformationResponse(
                updatePersonalInformationUseCase.updatePersonalInformation(command).value());
        return ResponseEntity.ok(response);
    }

}
