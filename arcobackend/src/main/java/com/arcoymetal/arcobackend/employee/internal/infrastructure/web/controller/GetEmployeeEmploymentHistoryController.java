package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmploymentPeriodDetails;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.GetEmployeeEmploymentHistoryUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.query.GetEmployeeEmploymentHistoryQuery;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class GetEmployeeEmploymentHistoryController {

    private final GetEmployeeEmploymentHistoryUseCase getEmployeeEmploymentHistoryUseCase;

    @GetMapping("/{id}/employment-periods")
    public ResponseEntity<List<EmploymentPeriodDetails>> getEmploymentHistory(@PathVariable UUID id) {
        List<EmploymentPeriodDetails> history = getEmployeeEmploymentHistoryUseCase.getEmploymentHistory(
                new GetEmployeeEmploymentHistoryQuery(id)
        );
        return ResponseEntity.ok(history);
    }
}
