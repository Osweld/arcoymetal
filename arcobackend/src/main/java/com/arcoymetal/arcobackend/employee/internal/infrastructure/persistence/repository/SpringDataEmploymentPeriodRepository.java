package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentStatus;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmploymentPeriodEntity;

public interface SpringDataEmploymentPeriodRepository extends JpaRepository<EmploymentPeriodEntity, UUID> {

    boolean existsByEmployeeIdAndStatus(UUID employeeId, EmploymentStatus status);

    boolean existsByEmployeeIdAndStatusAndEndDateGreaterThanEqual(
            UUID employeeId,
            EmploymentStatus status,
            LocalDate startDate
    );

    List<EmploymentPeriodEntity> findByEmployeeIdOrderByStartDateDescCreatedAtDesc(UUID employeeId);

    List<EmploymentPeriodEntity> findByStatusAndStartDateLessThanEqual(
            EmploymentStatus status,
            LocalDate referenceDate
    );

    Optional<EmploymentPeriodEntity> findByEmployeeIdAndStatusAndStartDateLessThanEqual(
            UUID employeeId,
            EmploymentStatus status,
            LocalDate referenceDate
    );

}
