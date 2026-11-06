package com.arcoymetal.arcobackend.identityaccess.internal.application.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.arcoymetal.arcobackend.identityaccess.internal.application.command.CreateUserCommand;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.CreateUserUseCase;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.PasswordHasher;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.UserRepository;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.User;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Email;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.EmployeeId;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.PasswordHash;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserId;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateUserService implements CreateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    @Override
    public UserId createUser(CreateUserCommand command) {

        Objects.requireNonNull(command, "CreateUserCommand cannot be null");


        // TODO: Implement the logic to validate employeeId existence in the employee
        // service before creating the user.
        // For example, you could call an EmployeeService to check if the employee
        // exists.

        UserId userId = UserId.generate();
        PasswordHash passwordHash = passwordHasher.hash(command.password());

        User user = User.create(
                userId,
                new EmployeeId(command.employeeId()),
                passwordHash,
                new Email(command.email()),
                command.roles());
        userRepository.save(user);
        return userId;
    }

}
