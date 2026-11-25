package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.employee.internal.application.command.RegisterEmploymentChangeCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.RegisterEmploymentChangeUseCase;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.mapper.EmploymentPeriodRequestMapper;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request.RegisterEmploymentChangeRequest;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.response.RegisterEmploymentChangeResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/employment-periods")
@RequiredArgsConstructor
public class RegisterEmploymentChangeController {

    private final RegisterEmploymentChangeUseCase registerEmploymentChangeUseCase;
    private final EmploymentPeriodRequestMapper employmentPeriodRequestMapper;

    @PostMapping("/{id}/changes")
    public ResponseEntity<RegisterEmploymentChangeResponse> registerChange(
            @PathVariable UUID id,
            @Valid @RequestBody RegisterEmploymentChangeRequest request
    ) {
        RegisterEmploymentChangeCommand command =
                employmentPeriodRequestMapper.toRegisterChangeCommand(id, request);
        UUID changeId = registerEmploymentChangeUseCase.registerChange(command).value();
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterEmploymentChangeResponse(changeId));
    }
}
