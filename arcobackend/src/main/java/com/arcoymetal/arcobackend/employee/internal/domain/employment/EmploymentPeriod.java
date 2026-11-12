package com.arcoymetal.arcobackend.employee.internal.domain.employment;

import java.util.List;
import java.util.Objects;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.EmployeeId;

public class EmploymentPeriod {
    private final EmploymentId employmentId;
    private final EmployeeId employeeId;
    private StartDate startDate;
    private EndDate endDate;
    private EmploymentStatus status;
    private TerminationReason terminationReason;
    private List<EmploymentChange> employmentChanges;

    private EmploymentPeriod(
            EmploymentId employmentId,
            EmployeeId employeeId,
            StartDate startDate,
            EndDate endDate,
            EmploymentStatus status,
            TerminationReason terminationReason,
            List<EmploymentChange> employmentChanges
    ) {
        this.employmentId = Objects.requireNonNull(employmentId, "EmploymentId cannot be null");
        this.employeeId = Objects.requireNonNull(employeeId, "EmployeeId cannot be null");
        this.startDate = Objects.requireNonNull(startDate, "StartDate cannot be null");
        this.endDate = endDate; // End date can be null for active employees
        this.status = Objects.requireNonNull(status, "EmployeeStatus cannot be null");
        this.terminationReason = terminationReason; // Termination reason can be null for active employees
        this.employmentChanges = Objects.requireNonNull(employmentChanges, "EmploymentChanges cannot be null");
    }

    public static EmploymentPeriod create(
            EmploymentId employmentId,
            EmployeeId employeeId,
            StartDate startDate,
            EndDate endDate,
            EmploymentStatus status,
            TerminationReason terminationReason,
            List<EmploymentChange> employmentChanges
    ) {
        return new EmploymentPeriod(employmentId, employeeId, startDate, endDate, status, terminationReason, employmentChanges);
    }


}