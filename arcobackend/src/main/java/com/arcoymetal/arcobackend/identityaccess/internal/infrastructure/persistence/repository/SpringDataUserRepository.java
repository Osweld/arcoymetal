package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.persistence.entity.UserEntity;

public interface SpringDataUserRepository extends JpaRepository<UserEntity, UUID> {

    boolean existsByEmail(String email);

    boolean existsByEmployeeId(UUID employeeId);

    Optional<UserEntity> findByEmployeeId(UUID employeeId);

    @Query("SELECT u.employeeId FROM UserEntity u")
    List<UUID> findAllLinkedEmployeeIds();

}
