package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmployeeEntity;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.projection.EmployeeDetailsProjection;

public interface SpringDataEmployeeRepository extends JpaRepository<EmployeeEntity, UUID> {

    boolean existsByEmail(String email);
    boolean existsByDui(String dui);
    Optional<EmployeeDetailsProjection> findProjectedById(UUID employeeId);
    Page<EmployeeDetailsProjection> findAllProjectedBy(Pageable pageable);

}
