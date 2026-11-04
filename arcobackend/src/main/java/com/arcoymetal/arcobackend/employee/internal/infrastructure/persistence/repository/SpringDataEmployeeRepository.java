package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmployeeEntity;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.projection.EmployeeDetailsProjection;

public interface SpringDataEmployeeRepository extends JpaRepository<EmployeeEntity, UUID> {

    boolean existsByEmail(String email);
    boolean existsByDui(String dui);
    List<EmployeeDetailsProjection> findAllProjectedBy();

}
