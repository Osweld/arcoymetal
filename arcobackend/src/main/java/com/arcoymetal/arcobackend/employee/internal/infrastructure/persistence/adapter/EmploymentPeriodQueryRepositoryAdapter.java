package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmploymentChangeDetails;
import com.arcoymetal.arcobackend.employee.internal.application.dto.EmploymentPeriodDetails;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmploymentPeriodQueryRepository;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmploymentChangeEntity;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmploymentPeriodEntity;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository.SpringDataEmploymentChangeRepository;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository.SpringDataEmploymentPeriodRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmploymentPeriodQueryRepositoryAdapter implements EmploymentPeriodQueryRepository {

    private final SpringDataEmploymentPeriodRepository springDataEmploymentPeriodRepository;
    private final SpringDataEmploymentChangeRepository springDataEmploymentChangeRepository;

    @Override
    public List<EmploymentPeriodDetails> findByEmployeeIdOrderByStartDateDesc(UUID employeeId) {
        List<EmploymentPeriodEntity> periods = springDataEmploymentPeriodRepository
                .findByEmployeeIdOrderByStartDateDescCreatedAtDesc(employeeId);

        return periods.stream()
                .map(period -> {
                    List<EmploymentChangeDetails> changes = springDataEmploymentChangeRepository
                            .findByEmploymentIdOrderByEffectiveFromDesc(period.getEmploymentId())
                            .stream()
                            .map(this::toChangeDetails)
                            .toList();
                    return new EmploymentPeriodDetails(
                            period.getEmploymentId(),
                            period.getEmployeeId(),
                            period.getStartDate(),
                            period.getEndDate(),
                            period.getStatus(),
                            period.getTerminationReason(),
                            changes
                    );
                })
                .toList();
    }

    private EmploymentChangeDetails toChangeDetails(EmploymentChangeEntity entity) {
        return new EmploymentChangeDetails(
                entity.getEmploymentChangeId(),
                entity.getJobTitle(),
                entity.getContractType(),
                entity.getBaseSalary(),
                entity.getEffectiveFrom(),
                entity.getCreatedAt()
        );
    }
}
