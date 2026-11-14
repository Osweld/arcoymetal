package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.ContractType;
import com.arcoymetal.arcobackend.shared.infrastructure.persistence.entity.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@Entity
@Table(
    name = "employment_change",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_employment_change_effective_date",
            columnNames = {"employment_id", "effective_from"}
        )
    },
    indexes = {
        @Index(
            name = "idx_employment_change_period",
            columnList = "employment_id"
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor()
public class EmploymentChangeEntity extends AuditableEntity {

    @Id
    @Column(name = "employment_change_id", nullable = false, updatable = false)
    private UUID employmentChangeId;

    @Column(name = "employment_id", nullable = false, updatable = false)
    private UUID employmentId;

    @Column(name = "job_title", nullable = false, length = 100)
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", nullable = false, length = 30)
    private ContractType contractType;

    @Column(name = "base_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;
}
