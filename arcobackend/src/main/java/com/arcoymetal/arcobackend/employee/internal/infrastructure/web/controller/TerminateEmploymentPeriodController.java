package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.employee.internal.application.command.TerminateEmploymentPeriodCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.TerminateEmploymentPeriodUseCase;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.mapper.EmploymentPeriodRequestMapper;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request.TerminateEmploymentPeriodRequest;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.response.TerminateEmploymentPeriodResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/employment-periods")
@RequiredArgsConstructor
public class TerminateEmploymentPeriodController {

    private final TerminateEmploymentPeriodUseCase terminateEmploymentPeriodUseCase;
    private final EmploymentPeriodRequestMapper employmentPeriodRequestMapper;

    @PostMapping("/{id}/terminate")
    public ResponseEntity<TerminateEmploymentPeriodResponse> terminateEmploymentPeriod(
            @PathVariable UUID id,
            @Valid @RequestBody TerminateEmploymentPeriodRequest request
    ) {
        TerminateEmploymentPeriodCommand command =
                employmentPeriodRequestMapper.toTerminateCommand(id, request);
        UUID terminatedId = terminateEmploymentPeriodUseCase.terminateEmploymentPeriod(command).value();
        return ResponseEntity.ok(new TerminateEmploymentPeriodResponse(terminatedId));
    }
}
