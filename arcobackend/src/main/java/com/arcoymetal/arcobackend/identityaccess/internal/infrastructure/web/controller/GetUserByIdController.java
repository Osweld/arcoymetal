package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.UserSummary;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.GetUserByIdUseCase;
import com.arcoymetal.arcobackend.identityaccess.internal.application.query.GetUserByIdQuery;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class GetUserByIdController {

    private final GetUserByIdUseCase getUserByIdUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<UserSummary> getUserById(@PathVariable UUID id) {
        UserSummary user = getUserByIdUseCase.getUserById(new GetUserByIdQuery(id));
        return ResponseEntity.ok(user);
    }

}
