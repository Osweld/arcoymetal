package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmployeeEntity;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.projection.EmployeeDetailsProjection;

public interface SpringDataEmployeeRepository extends JpaRepository<EmployeeEntity, UUID> {

    boolean existsByEmail(String email);

    boolean existsByDui(String dui);

    boolean existsByNit(String nit);

    boolean existsByIsss(String isss);

    boolean existsByNup(String nup);

    Optional<EmployeeDetailsProjection> findProjectedById(UUID employeeId);

    Page<EmployeeDetailsProjection> findAllProjectedBy(Pageable pageable);

    @Query("""
            SELECT e FROM EmployeeEntity e
            WHERE (:searchPattern IS NULL
                OR LOWER(e.firstName) LIKE :searchPattern
                OR LOWER(e.lastName) LIKE :searchPattern
                OR LOWER(CONCAT(e.firstName, ' ', e.lastName)) LIKE :searchPattern
                OR LOWER(e.email) LIKE :searchPattern
                OR LOWER(e.dui) LIKE :duiPattern)
              AND (:employmentStatus IS NULL
                OR (:employmentStatus = 'ACTIVE' AND EXISTS (
                    SELECT 1 FROM EmploymentPeriodEntity ep
                    WHERE ep.employeeId = e.id AND ep.status = com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentStatus.ACTIVE
                ))
                OR (:employmentStatus = 'TERMINATED' AND NOT EXISTS (
                    SELECT 1 FROM EmploymentPeriodEntity ep
                    WHERE ep.employeeId = e.id AND ep.status = com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentStatus.ACTIVE
                ) AND EXISTS (
                    SELECT 1 FROM EmploymentPeriodEntity ep
                    WHERE ep.employeeId = e.id AND ep.status = com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentStatus.TERMINATED
                ))
                OR (:employmentStatus = 'WITHOUT_PERIOD' AND NOT EXISTS (
                    SELECT 1 FROM EmploymentPeriodEntity ep
                    WHERE ep.employeeId = e.id
                )))
            """)
    Page<EmployeeDetailsProjection> searchProjected(
            @Param("searchPattern") String searchPattern,
            @Param("duiPattern") String duiPattern,
            @Param("employmentStatus") String employmentStatus,
            Pageable pageable
    );
}
