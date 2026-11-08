package com.arcoymetal.arcobackend.identityaccess.internal.application.port.in;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.PageQuery;
import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.PageResult;
import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.UserSummary;

public interface GetAllUserWithPaginationUseCase {

    PageResult<UserSummary> getAllUsers(PageQuery pageQuery);

}
