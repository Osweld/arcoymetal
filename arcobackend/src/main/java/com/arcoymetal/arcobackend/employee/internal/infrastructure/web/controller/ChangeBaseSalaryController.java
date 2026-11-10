package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.employee.internal.application.command.ChangeBaseSalaryCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.ChangeBaseSalaryUseCase;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.mapper.EmployeeRequestMapper;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request.ChangeBaseSalaryRequest;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.web.response.ChangeBaseSalaryResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class ChangeBaseSalaryController {

    private final ChangeBaseSalaryUseCase changeBaseSalaryUseCase;
    private final EmployeeRequestMapper employeeRequestMapper;

    @PutMapping("/{id}/change-base-salary")
    public ResponseEntity<ChangeBaseSalaryResponse> changeBaseSalary(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeBaseSalaryRequest request) {
        ChangeBaseSalaryCommand command = employeeRequestMapper.toChangeBaseSalaryCommand(request, id);
        ChangeBaseSalaryResponse response = new ChangeBaseSalaryResponse(changeBaseSalaryUseCase.changeBaseSalary(command).value());

        return ResponseEntity.ok(response);
    }


}
