package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.employee.internal.application.command.RemoveEmploymentChangeCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.RemoveEmploymentChangeUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/employment-periods")
@RequiredArgsConstructor
public class RemoveEmploymentChangeController {

    private final RemoveEmploymentChangeUseCase removeEmploymentChangeUseCase;

    @DeleteMapping("/{periodId}/changes/{changeId}")
    public ResponseEntity<Void> removeChange(
            @PathVariable UUID periodId,
            @PathVariable UUID changeId
    ) {
        removeEmploymentChangeUseCase.removeChange(new RemoveEmploymentChangeCommand(periodId, changeId));
        return ResponseEntity.noContent().build();
    }
}
