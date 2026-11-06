package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.mapper;

import org.springframework.stereotype.Component;

import com.arcoymetal.arcobackend.identityaccess.internal.application.command.CreateUserCommand;
import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.request.CreateUserRequest;

@Component 
public class UserRequestMapper {

    public CreateUserCommand toCommand(CreateUserRequest request) {
        return new CreateUserCommand(
                request.password(),
                request.email(),
                request.employeeId(),
                request.roles());
    }

}
