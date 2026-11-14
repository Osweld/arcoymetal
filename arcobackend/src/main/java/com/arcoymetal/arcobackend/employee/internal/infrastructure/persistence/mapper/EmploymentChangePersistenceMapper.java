package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.BaseSalary;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EffectiveFrom;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentChange;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentChangeId;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentId;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.JobTitle;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmploymentChangeEntity;

@Component 
public class EmploymentChangePersistenceMapper {

    public EmploymentChangeEntity toEntity(EmploymentChange employmentChange) {
        return new EmploymentChangeEntity(
                employmentChange.getEmploymentChangeId().value(),
                employmentChange.getEmploymentId().value(),
                employmentChange.getJobTitle().value(),
                employmentChange.getContractType(),
                employmentChange.getBaseSalary().value(),
                employmentChange.getEffectiveFrom().value()
        );
    }

    public EmploymentChange toDomain(EmploymentChangeEntity entity) {
        return EmploymentChange.reconstitute(
            new EmploymentChangeId(entity.getEmploymentChangeId()),
            new EmploymentId(entity.getEmploymentId()),
            new JobTitle(entity.getJobTitle()),
            entity.getContractType(),
            new BaseSalary(entity.getBaseSalary()),
            new EffectiveFrom(entity.getEffectiveFrom())
        );
    }

    public List<EmploymentChangeEntity> toEmploymentChangeEntities(List<EmploymentChange> employmentChanges) {
        return employmentChanges.stream()
                .map(this::toEntity)
                .toList();
    }

    public List<EmploymentChange> toDomainList(List<EmploymentChangeEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

}


