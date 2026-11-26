package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.GetLinkedEmployeeIdsUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class GetLinkedEmployeeIdsController {

    private final GetLinkedEmployeeIdsUseCase getLinkedEmployeeIdsUseCase;

    @GetMapping("/linked-employee-ids")
    public ResponseEntity<List<UUID>> getLinkedEmployeeIds() {
        return ResponseEntity.ok(getLinkedEmployeeIdsUseCase.getLinkedEmployeeIds());
    }
}
