package com.arcoymetal.arcobackend.identityaccess.internal.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.GetLinkedEmployeeIdsUseCase;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.UserQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetLinkedEmployeeIdsService implements GetLinkedEmployeeIdsUseCase {

    private final UserQueryRepository userQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UUID> getLinkedEmployeeIds() {
        return userQueryRepository.findAllLinkedEmployeeIds();
    }
}
