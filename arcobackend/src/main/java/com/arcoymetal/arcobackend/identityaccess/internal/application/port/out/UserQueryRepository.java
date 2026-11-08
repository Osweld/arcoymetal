package com.arcoymetal.arcobackend.identityaccess.internal.application.port.out;

import java.util.UUID;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.PageQuery;
import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.PageResult;
import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.UserSummary;

public interface UserQueryRepository {

    PageResult<UserSummary> findAll(PageQuery pageQuery);
    UserSummary findById(UUID userId);
    
}
