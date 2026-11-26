package com.arcoymetal.arcobackend.identityaccess.internal.application.port.in;

import java.util.UUID;

public interface LogoutUseCase {

    void logout(UUID authenticatedUserId, String refreshToken);

}
