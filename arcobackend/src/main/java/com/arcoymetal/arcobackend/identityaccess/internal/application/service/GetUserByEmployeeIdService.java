package com.arcoymetal.arcobackend.identityaccess.internal.application.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.UserSummary;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.GetUserByEmployeeIdUseCase;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.UserQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetUserByEmployeeIdService implements GetUserByEmployeeIdUseCase {

    private final UserQueryRepository userQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public UserSummary getUserByEmployeeId(UUID employeeId) {
        Objects.requireNonNull(employeeId, "employeeId cannot be null");
        return userQueryRepository.findByEmployeeId(employeeId);
    }
}
