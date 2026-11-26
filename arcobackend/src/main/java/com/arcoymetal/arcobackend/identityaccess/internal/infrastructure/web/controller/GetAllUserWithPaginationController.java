package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.PageQuery;
import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.PageResult;
import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.UserSummary;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.GetAllUserWithPaginationUseCase;
import com.arcoymetal.arcobackend.identityaccess.internal.application.query.SearchUsersQuery;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class GetAllUserWithPaginationController {

    private final GetAllUserWithPaginationUseCase getAllUserWithPaginationUseCase;

    @GetMapping
    public ResponseEntity<PageResult<UserSummary>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UserStatus status
    ) {
        SearchUsersQuery query = new SearchUsersQuery(new PageQuery(page, size), search, status);
        PageResult<UserSummary> result = getAllUserWithPaginationUseCase.getAllUsers(query);
        return ResponseEntity.ok(result);
    }

}
