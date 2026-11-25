package com.arcoymetal.arcobackend.employee.internal.infrastructure.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.employee.api.ActiveEmployeeCompensationProvider;
import com.arcoymetal.arcobackend.employee.api.EmployeeCompensationSnapshot;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentStatus;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmployeeEntity;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmploymentChangeEntity;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmploymentPeriodEntity;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository.SpringDataEmployeeRepository;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository.SpringDataEmploymentChangeRepository;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository.SpringDataEmploymentPeriodRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ActiveEmployeeCompensationProviderImpl implements ActiveEmployeeCompensationProvider {

    private final SpringDataEmployeeRepository employeeRepository;
    private final SpringDataEmploymentPeriodRepository employmentPeriodRepository;
    private final SpringDataEmploymentChangeRepository employmentChangeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeCompensationSnapshot> findActiveCompensationsAt(LocalDate referenceDate) {
        Objects.requireNonNull(referenceDate, "referenceDate cannot be null");

        List<EmploymentPeriodEntity> activePeriods = employmentPeriodRepository
                .findByStatusAndStartDateLessThanEqual(EmploymentStatus.ACTIVE, referenceDate);

        if (activePeriods.isEmpty()) {
            return List.of();
        }

        List<UUID> employeeIds = activePeriods.stream()
                .map(EmploymentPeriodEntity::getEmployeeId)
                .toList();
        Map<UUID, EmployeeEntity> employeesById = employeeRepository.findAllById(employeeIds).stream()
                .collect(Collectors.toMap(EmployeeEntity::getId, Function.identity()));

        return activePeriods.stream()
                .map(period -> resolveSnapshot(employeesById.get(period.getEmployeeId()), period, referenceDate))
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmployeeCompensationSnapshot> findCompensationAt(UUID employeeId, LocalDate referenceDate) {
        Objects.requireNonNull(employeeId, "employeeId cannot be null");
        Objects.requireNonNull(referenceDate, "referenceDate cannot be null");

        return employmentPeriodRepository
                .findByEmployeeIdAndStatusAndStartDateLessThanEqual(employeeId, EmploymentStatus.ACTIVE, referenceDate)
                .flatMap(period -> employeeRepository.findById(employeeId)
                        .flatMap(employee -> resolveSnapshot(employee, period, referenceDate)));
    }

    private Optional<EmployeeCompensationSnapshot> resolveSnapshot(
            EmployeeEntity employee,
            EmploymentPeriodEntity period,
            LocalDate referenceDate
    ) {
        if (employee == null) {
            return Optional.empty();
        }
        return employmentChangeRepository
                .findFirstByEmploymentIdAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
                        period.getEmploymentId(),
                        referenceDate
                )
                .map(change -> toSnapshot(employee, period, change));
    }

    private EmployeeCompensationSnapshot toSnapshot(
            EmployeeEntity employee,
            EmploymentPeriodEntity period,
            EmploymentChangeEntity change
    ) {
        return new EmployeeCompensationSnapshot(
                employee.getId(),
                employee.getFirstName() + " " + employee.getLastName(),
                employee.getDui(),
                employee.getNit(),
                employee.getIsss(),
                employee.getNup(),
                employee.getPensionFundInstitution().name(),
                period.getEmploymentId(),
                period.getStartDate(),
                change.getJobTitle(),
                change.getContractType().name(),
                change.getBaseSalary(),
                change.getEffectiveFrom()
        );
    }
}
