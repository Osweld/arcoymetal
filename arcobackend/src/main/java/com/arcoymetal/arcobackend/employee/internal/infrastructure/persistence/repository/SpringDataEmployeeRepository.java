package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmployeeEntity;

public interface SpringDataEmployeeRepository extends JpaRepository<EmployeeEntity, UUID> {

    boolean existsByEmail(String email);
    boolean existsByDui(String dui);

}
