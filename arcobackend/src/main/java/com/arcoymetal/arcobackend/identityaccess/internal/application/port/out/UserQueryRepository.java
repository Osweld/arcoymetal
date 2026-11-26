package com.arcoymetal.arcobackend.identityaccess.internal.application.port.out;

import java.util.List;
import java.util.UUID;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.PageResult;
import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.UserSummary;
import com.arcoymetal.arcobackend.identityaccess.internal.application.query.SearchUsersQuery;

public interface UserQueryRepository {

    PageResult<UserSummary> findAll(SearchUsersQuery query);

    UserSummary findById(UUID userId);

    UserSummary findByEmployeeId(UUID employeeId);

    List<UUID> findAllLinkedEmployeeIds();
}
