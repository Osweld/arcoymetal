package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity;

import java.time.LocalDate;
import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentStatus;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.TerminationReason;
import com.arcoymetal.arcobackend.shared.infrastructure.persistence.entity.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@Entity
@Table(
    name = "employment_period",
    indexes = {
        @Index(
            name = "idx_employment_employee",
            columnList = "employee_id"
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor 
public class EmploymentPeriodEntity extends AuditableEntity {

    @Id
    @Column(name = "employment_id", nullable = false, updatable = false)
    private UUID employmentId;

    @Column(name = "employee_id", nullable = false, updatable = false)
    private UUID employeeId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private EmploymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "termination_reason", length = 50)
    private TerminationReason terminationReason;
}