package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.identityaccess.internal.application.command.UpdateUserCommand;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.UpdateUserUseCase;
import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.request.UpdateUserRequest;
import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.response.UpdateUserResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UpdateUserController {

    private final UpdateUserUseCase updateUserUseCase;

    @PutMapping("/{id}")
    public ResponseEntity<UpdateUserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        UpdateUserCommand command = new UpdateUserCommand(
                id,
                request.email(),
                request.roles(),
                request.status()
        );
        UUID updatedUserId = updateUserUseCase.updateUser(command).value();
        return ResponseEntity.ok(new UpdateUserResponse(updatedUserId));
    }
}
