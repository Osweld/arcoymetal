package com.arcoymetal.arcobackend.identityaccess.internal.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.UserSummary;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.GetUserByIdUseCase;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.UserQueryRepository;
import com.arcoymetal.arcobackend.identityaccess.internal.application.query.GetUserByIdQuery;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class GetUserByIdService implements GetUserByIdUseCase {

    private final UserQueryRepository userQueryRepository;

   

    @Override
    @Transactional(readOnly = true)
    public UserSummary getUserById(GetUserByIdQuery query) {
        return userQueryRepository.findById(query.userId());
    }

}
