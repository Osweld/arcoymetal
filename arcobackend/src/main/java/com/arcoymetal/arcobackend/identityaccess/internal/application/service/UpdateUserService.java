package com.arcoymetal.arcobackend.identityaccess.internal.application.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.identityaccess.internal.application.command.UpdateUserCommand;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.UpdateUserUseCase;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.UserRepository;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.User;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.DuplicateUserException;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.exception.LastAdministratorCannotBeModifiedException;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Email;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Role;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserId;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateUserService implements UpdateUserUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserId updateUser(UpdateUserCommand command) {
        Objects.requireNonNull(command, "UpdateUserCommand cannot be null");

        User existing = userRepository.findById(command.userId());
        boolean wasActiveAdmin = existing.isActiveAdministrator();

        Email newEmail = new Email(command.email());
        if (!newEmail.equals(existing.getEmail()) && userRepository.existsByEmail(newEmail.value())) {
            throw new DuplicateUserException("A user with the provided email already exists.");
        }

        existing.updateAccount(newEmail, command.roles(), command.status());

        if (wasActiveAdmin && !existing.isActiveAdministrator()) {
            long activeAdminCount = userRepository.countActiveByRole(Role.ADMINISTRATOR);
            if (activeAdminCount <= 1) {
                throw new LastAdministratorCannotBeModifiedException(
                        "Cannot disable, lock, or remove the ADMINISTRATOR role from the last active administrator."
                );
            }
        }

        userRepository.save(existing);
        return existing.getUserId();
    }
}
