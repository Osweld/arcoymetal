package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.persistence.entity.UserEntity;

public interface SpringDataUserRepository extends JpaRepository<UserEntity, UUID> {

}
