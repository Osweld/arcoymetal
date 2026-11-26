package com.arcoymetal.arcobackend.identityaccess.internal.application.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.AuthenticatedUserView;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.GetCurrentUserUseCase;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.UserRepository;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetCurrentUserService implements GetCurrentUserUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public AuthenticatedUserView getCurrentUser(UUID userId) {
        Objects.requireNonNull(userId, "UserId cannot be null");
        User user = userRepository.findById(userId);
        return new AuthenticatedUserView(
                user.getUserId().value(),
                user.getEmployeeId().value(),
                user.getEmail().value(),
                user.getRoles(),
                user.getUserStatus(),
                user.isMustChangePassword()
        );
    }
}
