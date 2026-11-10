package com.arcoymetal.arcobackend.identityaccess.internal.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.PageQuery;
import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.PageResult;
import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.UserSummary;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.GetAllUserWithPaginationUseCase;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.UserQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAllUserWithPaginationService implements GetAllUserWithPaginationUseCase {

    private final UserQueryRepository userQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<UserSummary> getAllUsers(PageQuery pageQuery) {
        return userQueryRepository.findAll(pageQuery);
    }

}
