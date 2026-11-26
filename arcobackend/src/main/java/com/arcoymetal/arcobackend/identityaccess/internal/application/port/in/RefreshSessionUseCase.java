package com.arcoymetal.arcobackend.identityaccess.internal.application.port.in;

import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.AuthenticatedSession;

public interface RefreshSessionUseCase {

    AuthenticatedSession refresh(String refreshToken);

}
