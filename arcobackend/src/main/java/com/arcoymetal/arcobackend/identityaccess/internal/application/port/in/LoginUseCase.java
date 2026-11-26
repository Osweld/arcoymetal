package com.arcoymetal.arcobackend.identityaccess.internal.application.port.in;

import com.arcoymetal.arcobackend.identityaccess.internal.application.command.LoginCommand;
import com.arcoymetal.arcobackend.identityaccess.internal.application.dto.AuthenticatedSession;

public interface LoginUseCase {

    AuthenticatedSession login(LoginCommand command);

}
