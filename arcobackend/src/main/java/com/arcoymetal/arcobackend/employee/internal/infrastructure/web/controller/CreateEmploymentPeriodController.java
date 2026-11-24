package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.employee.internal.application.command.CreateEmploymentPeriodCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.CreateEmploymentPeriodUseCase;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.mapper.EmploymentPeriodRequestMapper;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request.CreateEmploymentPeriodRequest;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.response.CreateEmploymentPeriodResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/employment-periods")
@RequiredArgsConstructor
public class CreateEmploymentPeriodController {

    private final CreateEmploymentPeriodUseCase createEmploymentPeriodUseCase;
    private final EmploymentPeriodRequestMapper employmentPeriodRequestMapper;

    @PostMapping
    public ResponseEntity<CreateEmploymentPeriodResponse> createEmploymentPeriod(
            @Valid @RequestBody CreateEmploymentPeriodRequest request) {
        CreateEmploymentPeriodCommand command = employmentPeriodRequestMapper.toCommand(request);
        CreateEmploymentPeriodResponse response = new CreateEmploymentPeriodResponse(
                createEmploymentPeriodUseCase.createEmploymentPeriod(command).value());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
