package com.arcoymetal.arcobackend.identityaccess.internal.application.port.in;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.PageResult;
import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.UserSummary;
import com.arcoymetal.arcobackend.identityaccess.internal.application.query.SearchUsersQuery;

public interface GetAllUserWithPaginationUseCase {

    PageResult<UserSummary> getAllUsers(SearchUsersQuery query);

}
