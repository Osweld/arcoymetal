package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Role;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserStatus;
import com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.persistence.entity.UserEntity;

public interface SpringDataUserRepository extends JpaRepository<UserEntity, UUID> {

    boolean existsByEmail(String email);

    boolean existsByEmployeeId(UUID employeeId);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByEmployeeId(UUID employeeId);

    @Query("SELECT u.employeeId FROM UserEntity u")
    List<UUID> findAllLinkedEmployeeIds();

    @Query("SELECT COUNT(DISTINCT u) FROM UserEntity u JOIN u.roles r WHERE u.status = :status AND r = :role")
    long countByStatusAndRole(@Param("status") UserStatus status, @Param("role") Role role);

    @Query("""
            SELECT u FROM UserEntity u
            WHERE (:searchPattern IS NULL OR LOWER(u.email) LIKE :searchPattern)
              AND (:status IS NULL OR u.status = :status)
            """)
    Page<UserEntity> searchUsers(
            @Param("searchPattern") String searchPattern,
            @Param("status") UserStatus status,
            Pageable pageable
    );

}
