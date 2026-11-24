package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmploymentPeriodRepository;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentChange;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentPeriod;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentStatus;
import com.arcoymetal.arcobackend.employee.internal.domain.exception.EmploymentPeriodNotFoundException;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmploymentChangeEntity;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmploymentPeriodEntity;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.mapper.EmploymentChangePersistenceMapper;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.mapper.EmploymentPeriodPersistenceMapper;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository.SpringDataEmploymentChangeRepository;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository.SpringDataEmploymentPeriodRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmploymentPeriodPersistenceAdapter implements EmploymentPeriodRepository {

    private final SpringDataEmploymentPeriodRepository springDataEmploymentPeriodRepository;
    private final SpringDataEmploymentChangeRepository springDataEmploymentChangeRepository;
    private final EmploymentPeriodPersistenceMapper employmentPeriodPersistenceMapper;
    private final EmploymentChangePersistenceMapper employmentChangePersistenceMapper;

    @Override
    public void save(EmploymentPeriod employmentPeriod) {
        EmploymentPeriodEntity employmentPeriodEntity = employmentPeriodPersistenceMapper.toEntity(employmentPeriod);
        springDataEmploymentPeriodRepository.save(employmentPeriodEntity);

        List<EmploymentChangeEntity> employmentChangeEntities =
                employmentChangePersistenceMapper.toEmploymentChangeEntities(employmentPeriod.getEmploymentChanges());
        springDataEmploymentChangeRepository.saveAll(employmentChangeEntities);
    }

    @Override
    public EmploymentPeriod findById(UUID employmentId) {
        EmploymentPeriodEntity periodEntity = springDataEmploymentPeriodRepository.findById(employmentId)
                .orElseThrow(() -> new EmploymentPeriodNotFoundException(employmentId));
        List<EmploymentChangeEntity> changeEntities = springDataEmploymentChangeRepository
                .findByEmploymentId(employmentId)
                .orElse(List.of());
        List<EmploymentChange> changes = employmentChangePersistenceMapper.toDomainList(changeEntities);
        return employmentPeriodPersistenceMapper.toDomain(periodEntity, changes);
    }

    @Override
    public boolean existsActiveByEmployeeId(UUID employeeId) {
        return springDataEmploymentPeriodRepository.existsByEmployeeIdAndStatus(employeeId, EmploymentStatus.ACTIVE);
    }
}
