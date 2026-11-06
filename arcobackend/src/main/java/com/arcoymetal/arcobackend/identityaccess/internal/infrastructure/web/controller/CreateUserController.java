package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.identityaccess.internal.application.command.CreateUserCommand;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.CreateUserUseCase;
import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.mapper.UserRequestMapper;
import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.request.CreateUserRequest;
import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.response.CreateUserResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class CreateUserController {

    private final CreateUserUseCase createUserUseCase;
    private final UserRequestMapper userRequestMapper;

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        CreateUserCommand command = userRequestMapper.toCommand(request);
        CreateUserResponse response = new CreateUserResponse(createUserUseCase.createUser(command).value());
        return ResponseEntity.ok(response);
    }  

}
