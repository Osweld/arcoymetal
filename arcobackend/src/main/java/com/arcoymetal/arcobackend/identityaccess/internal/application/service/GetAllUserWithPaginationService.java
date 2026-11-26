package com.arcoymetal.arcobackend.identityaccess.internal.application.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.PageResult;
import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.UserSummary;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.GetAllUserWithPaginationUseCase;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.out.UserQueryRepository;
import com.arcoymetal.arcobackend.identityaccess.internal.application.query.SearchUsersQuery;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAllUserWithPaginationService implements GetAllUserWithPaginationUseCase {

    private final UserQueryRepository userQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<UserSummary> getAllUsers(SearchUsersQuery query) {
        Objects.requireNonNull(query, "SearchUsersQuery cannot be null");
        return userQueryRepository.findAll(query);
    }

}
