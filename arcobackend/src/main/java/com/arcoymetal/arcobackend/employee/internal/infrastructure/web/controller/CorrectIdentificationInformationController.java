package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.employee.internal.application.command.CorrectIdentificationInformationCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.CorrectIdentificationInformationUseCase;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.mapper.EmployeeRequestMapper;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request.CorrectIdentificationInformationRequest;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.response.CorrectIdentificationInformationResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class CorrectIdentificationInformationController {

     private final CorrectIdentificationInformationUseCase correctIdentificationInformationUseCase;
    private final EmployeeRequestMapper employeeRequestMapper;

    @PutMapping("/{id}/correct-identification-information")
    public ResponseEntity<CorrectIdentificationInformationResponse> correctIdentificationInformation(
            @PathVariable UUID id,
            @Valid @RequestBody CorrectIdentificationInformationRequest entity) {
        CorrectIdentificationInformationCommand command = employeeRequestMapper.toCorrectIdentificationInformationCommand(entity, id);
        CorrectIdentificationInformationResponse response = new CorrectIdentificationInformationResponse(correctIdentificationInformationUseCase.correctIdentificationInformation(command).value());

        return ResponseEntity.ok(response);
    }

}
