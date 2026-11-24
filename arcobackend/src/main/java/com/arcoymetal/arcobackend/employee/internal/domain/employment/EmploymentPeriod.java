package com.arcoymetal.arcobackend.employee.internal.domain.employment;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.EmployeeId;

public class EmploymentPeriod {
    private final EmploymentId employmentId;
    private final EmployeeId employeeId;
    private final StartDate startDate;
    private final EndDate endDate;
    private final EmploymentStatus status;
    private final TerminationReason terminationReason;
    private final List<EmploymentChange> employmentChanges;

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
        this.status = Objects.requireNonNull(status, "EmploymentStatus cannot be null");
        Objects.requireNonNull(employmentChanges, "EmploymentChanges cannot be null");

        if (status == EmploymentStatus.ACTIVE) {
            if (endDate != null || terminationReason != null) {
                throw new IllegalArgumentException(
                        "Active employment period cannot have endDate or terminationReason");
            }
        } else if (status == EmploymentStatus.TERMINATED) {
            if (endDate == null || terminationReason == null) {
                throw new IllegalArgumentException(
                        "Terminated employment period must have endDate and terminationReason");
            }
            if (endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("End date cannot be before start date");
            }
        }

        List<EmploymentChange> immutableChanges = List.copyOf(employmentChanges);
        Set<EffectiveFrom> effectiveDates = new HashSet<>();
        for (EmploymentChange change : immutableChanges) {
            if (!change.getEmploymentId().equals(this.employmentId)) {
                throw new IllegalArgumentException(
                        "EmploymentChange must belong to the same EmploymentPeriod");
            }
            if (!effectiveDates.add(change.getEffectiveFrom())) {
                throw new IllegalArgumentException(
                        "Duplicate effectiveFrom date in EmploymentPeriod changes: "
                                + change.getEffectiveFrom().value());
            }
        }

        this.endDate = endDate;
        this.terminationReason = terminationReason;
        this.employmentChanges = immutableChanges;
    }

    public static EmploymentPeriod create(
            EmploymentId employmentId,
            EmployeeId employeeId,
            StartDate startDate,
            List<EmploymentChange> employmentChanges
    ) {
        Objects.requireNonNull(employmentChanges, "EmploymentChanges cannot be null");
        if (employmentChanges.isEmpty()) {
            throw new IllegalArgumentException("EmploymentPeriod must have at least one initial EmploymentChange");
        }
        return new EmploymentPeriod(
                employmentId,
                employeeId,
                startDate,
                null,
                EmploymentStatus.ACTIVE,
                null,
                employmentChanges
        );
    }

    public static EmploymentPeriod reconstitute(
            EmploymentId employmentId,
            EmployeeId employeeId,
            StartDate startDate,
            EndDate endDate,
            EmploymentStatus status,
            TerminationReason terminationReason,
            List<EmploymentChange> employmentChanges
    ) {
        return new EmploymentPeriod(
                employmentId,
                employeeId,
                startDate,
                endDate,
                status,
                terminationReason,
                employmentChanges
        );
    }

    public EmploymentId getEmploymentId() {
        return employmentId;
    }

    public EmployeeId getEmployeeId() {
        return employeeId;
    }

    public StartDate getStartDate() {
        return startDate;
    }

    public EndDate getEndDate() {
        return endDate;
    }

    public EmploymentStatus getStatus() {
        return status;
    }

    public TerminationReason getTerminationReason() {
        return terminationReason;
    }

    public List<EmploymentChange> getEmploymentChanges() {
        return employmentChanges;
    }
}
