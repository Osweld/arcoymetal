package com.arcoymetal.arcobackend.identityaccess.internal.application.port.in;

import com.arcoymetal.arcobackend.identityaccess.internal.application.command.CreateUserCommand;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserId;

public interface CreateUserUseCase {

    UserId createUser(CreateUserCommand command);

}
