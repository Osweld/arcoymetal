package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.EmployeeId;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentChange;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentId;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentPeriod;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EndDate;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.StartDate;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmploymentPeriodEntity;

@Component
public class EmploymentPeriodPersistenceMapper {

    public EmploymentPeriodEntity toEntity(EmploymentPeriod employmentPeriod) {
        return new EmploymentPeriodEntity(
                employmentPeriod.getEmploymentId().value(),
                employmentPeriod.getEmployeeId().value(),
                employmentPeriod.getStartDate().value(),
                employmentPeriod.getEndDate().value() != null ? employmentPeriod.getEndDate().value() : null,
                employmentPeriod.getStatus(),
                employmentPeriod.getTerminationReason() != null ? employmentPeriod.getTerminationReason() : null);
    }

    public EmploymentPeriod toDomain(EmploymentPeriodEntity entity, List<EmploymentChange> employmentChanges) {
        return EmploymentPeriod.reconstitute(
            new EmploymentId(entity.getEmploymentId()),
            new EmployeeId(entity.getEmployeeId()),
            new StartDate(entity.getStartDate()),
            entity.getEndDate() != null ? new EndDate(entity.getEndDate()) : null,
            entity.getStatus(),
            entity.getTerminationReason() != null ? entity.getTerminationReason() : null,
            employmentChanges
        );
    }
}
