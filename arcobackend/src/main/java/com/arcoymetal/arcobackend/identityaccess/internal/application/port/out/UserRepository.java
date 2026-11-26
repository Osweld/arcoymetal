package com.arcoymetal.arcobackend.identityaccess.internal.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.User;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Role;

public interface UserRepository {

    void save(User user);
    User findById(UUID userId);
    Optional<User> findOptionalById(UUID userId);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmployeeId(UUID employeeId);
    long countActiveByRole(Role role);

}
