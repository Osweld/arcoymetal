package com.arcoymetal.arcobackend.identityaccess.internal.application.port.out;

import java.util.UUID;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.User;

public interface UserRepository {

    void save(User user);
    boolean existsByEmail(String email);
    boolean existsByEmployeeId(UUID employeeId);

}
