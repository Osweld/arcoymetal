package com.arcoymetal.arcobackend.identityaccess.internal.application.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.employee.api.EmployeeExistenceVerifier;
import com.arcoymetal.arcobackend.identityaccess.internal.application.command.CreateUserCommand;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.CreateUserUseCase;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.PasswordHasher;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.UserRepository;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.User;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.DuplicateUserException;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.ReferencedEmployeeNotFoundException;
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
    private final EmployeeExistenceVerifier employeeExistenceVerifier;

    @Override
    @Transactional
    public UserId createUser(CreateUserCommand command) {

        Objects.requireNonNull(command, "CreateUserCommand cannot be null");

        EmployeeId employeeId = new EmployeeId(command.employeeId());
        Email email = new Email(command.email());
        if (command.password() == null || command.password().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }

        if (!employeeExistenceVerifier.existsByEmployeeId(employeeId.value())) {
            throw new ReferencedEmployeeNotFoundException(employeeId.value());
        }

        if (userRepository.existsByEmployeeId(employeeId.value())) {
            throw new DuplicateUserException("User with employeeId " + employeeId.value() + " already exists.");
        }

        if (userRepository.existsByEmail(email.value())) {
            throw new DuplicateUserException("User with email " + email.value() + " already exists.");
        }

        UserId userId = UserId.generate();
        PasswordHash passwordHash = passwordHasher.hash(command.password());

        User user = User.create(
                userId,
                employeeId,
                passwordHash,
                email,
                command.roles());
        userRepository.save(user);
        return userId;
    }
}
