package com.arcoymetal.arcobackend.identityaccess.internal.application.port.in;

import java.util.UUID;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.AuthenticatedUserView;

public interface GetCurrentUserUseCase {

    AuthenticatedUserView getCurrentUser(UUID userId);

}
