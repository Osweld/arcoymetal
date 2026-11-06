package com.arcoymetal.arcobackend.identityaccess.internal.application.port.out;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.PasswordHash;

public interface PasswordHasher {

    PasswordHash hash(String password);
    boolean matches(String password, PasswordHash passwordHash);
    

}
