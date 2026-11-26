package com.arcoymetal.arcobackend.identityaccess.internal.application.port.in;

import com.arcoymetal.arcobackend.identityaccess.internal.application.command.UpdateUserCommand;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserId;

public interface UpdateUserUseCase {

    UserId updateUser(UpdateUserCommand command);

}
