package com.arcoymetal.arcobackend.identityaccess.internal.application.port.out;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.User;

public interface UserRepository {

    void save(User user);

}
