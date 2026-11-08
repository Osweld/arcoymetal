package com.arcoymetal.arcobackend.identityaccess.internal.application.port.in;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.UserSummary;
import com.arcoymetal.arcobackend.identityaccess.internal.application.query.GetUserByIdQuery;

public interface GetUserByIdUseCase {

    UserSummary getUserById(GetUserByIdQuery query);

}
