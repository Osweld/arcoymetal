package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentStatus;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmploymentPeriodEntity;

public interface SpringDataEmploymentPeriodRepository extends JpaRepository<EmploymentPeriodEntity, UUID> {

    boolean existsByEmployeeIdAndStatus(UUID employeeId, EmploymentStatus status);

}
