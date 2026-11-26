package com.arcoymetal.arcobackend.identityaccess.internal.application.port.in;

import java.util.UUID;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.UserSummary;

public interface GetUserByEmployeeIdUseCase {

    UserSummary getUserByEmployeeId(UUID employeeId);
}
