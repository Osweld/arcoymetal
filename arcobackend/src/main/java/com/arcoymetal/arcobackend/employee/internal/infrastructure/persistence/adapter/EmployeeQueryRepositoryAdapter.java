package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeDetails;
import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeEmploymentStatusFilter;
import com.arcoymetal.arcobackend.employee.internal.application.dto.PageResult;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeQueryRepository;
import com.arcoymetal.arcobackend.employee.internal.application.query.SearchEmployeesQuery;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentStatus;
import com.arcoymetal.arcobackend.employee.internal.domain.exception.EmployeeNotFoundException;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmploymentChangeEntity;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmploymentPeriodEntity;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.projection.EmployeeDetailsProjection;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository.SpringDataEmployeeRepository;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository.SpringDataEmploymentChangeRepository;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository.SpringDataEmploymentPeriodRepository;

@Repository
public class EmployeeQueryRepositoryAdapter implements EmployeeQueryRepository {

    private final SpringDataEmployeeRepository springDataEmployeeRepository;
    private final SpringDataEmploymentPeriodRepository springDataEmploymentPeriodRepository;
    private final SpringDataEmploymentChangeRepository springDataEmploymentChangeRepository;

    public EmployeeQueryRepositoryAdapter(
            SpringDataEmployeeRepository springDataEmployeeRepository,
            SpringDataEmploymentPeriodRepository springDataEmploymentPeriodRepository,
            SpringDataEmploymentChangeRepository springDataEmploymentChangeRepository
    ) {
        this.springDataEmployeeRepository = springDataEmployeeRepository;
        this.springDataEmploymentPeriodRepository = springDataEmploymentPeriodRepository;
        this.springDataEmploymentChangeRepository = springDataEmploymentChangeRepository;
    }

    @Override
    public EmployeeDetails findById(UUID employeeId) {
        EmployeeDetailsProjection projection = springDataEmployeeRepository.findProjectedById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        Map<UUID, EmploymentSummary> summaries = loadEmploymentSummaries(List.of(employeeId));
        return toEmployeeDetails(projection, summaries.getOrDefault(employeeId, EmploymentSummary.EMPTY));
    }

    @Override
    public PageResult<EmployeeDetails> findAllWithPagination(SearchEmployeesQuery query) {
        Pageable pageable = PageRequest.of(
                query.pageQuery().page(),
                query.pageQuery().size(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<EmployeeDetailsProjection> employeePage;
        if (query.search() == null && query.employmentStatus() == null) {
            employeePage = springDataEmployeeRepository.findAllProjectedBy(pageable);
        } else {
            String searchPattern = query.search() != null
                    ? "%" + query.search().toLowerCase(Locale.ROOT) + "%"
                    : null;
            String duiPattern = query.search() != null
                    ? "%" + query.search().replace("-", "").toLowerCase(Locale.ROOT) + "%"
                    : null;
            String statusFilter = query.employmentStatus() != null
                    ? query.employmentStatus().name()
                    : null;

            employeePage = springDataEmployeeRepository.searchProjected(
                    searchPattern,
                    duiPattern,
                    statusFilter,
                    pageable
            );
        }

        List<UUID> employeeIds = employeePage.getContent().stream()
                .map(EmployeeDetailsProjection::getId)
                .toList();
        Map<UUID, EmploymentSummary> summaries = loadEmploymentSummaries(employeeIds);

        return new PageResult<>(
                employeePage.getContent().stream()
                        .map(projection -> toEmployeeDetails(
                                projection,
                                summaries.getOrDefault(projection.getId(), EmploymentSummary.EMPTY)))
                        .toList(),
                employeePage.getNumber(),
                employeePage.getSize(),
                employeePage.getTotalElements(),
                employeePage.getTotalPages(),
                employeePage.isFirst(),
                employeePage.isLast()
        );
    }

    private Map<UUID, EmploymentSummary> loadEmploymentSummaries(List<UUID> employeeIds) {
        if (employeeIds.isEmpty()) {
            return Map.of();
        }

        List<EmploymentPeriodEntity> periods = springDataEmploymentPeriodRepository.findByEmployeeIdIn(employeeIds);
        if (periods.isEmpty()) {
            return Map.of();
        }

        Map<UUID, List<EmploymentPeriodEntity>> periodsByEmployee = periods.stream()
                .collect(Collectors.groupingBy(EmploymentPeriodEntity::getEmployeeId));

        List<UUID> activePeriodIds = periods.stream()
                .filter(p -> p.getStatus() == EmploymentStatus.ACTIVE)
                .map(EmploymentPeriodEntity::getEmploymentId)
                .toList();

        Map<UUID, String> latestJobTitleByPeriodId = new HashMap<>();
        if (!activePeriodIds.isEmpty()) {
            List<EmploymentChangeEntity> changes = springDataEmploymentChangeRepository
                    .findByEmploymentIdInOrderByEffectiveFromDesc(activePeriodIds);
            for (EmploymentChangeEntity change : changes) {
                latestJobTitleByPeriodId.putIfAbsent(change.getEmploymentId(), change.getJobTitle());
            }
        }

        Map<UUID, EmploymentSummary> result = new HashMap<>();
        for (Map.Entry<UUID, List<EmploymentPeriodEntity>> entry : periodsByEmployee.entrySet()) {
            UUID empId = entry.getKey();
            List<EmploymentPeriodEntity> empPeriods = entry.getValue();

            EmploymentPeriodEntity activePeriod = empPeriods.stream()
                    .filter(p -> p.getStatus() == EmploymentStatus.ACTIVE)
                    .findFirst()
                    .orElse(null);

            if (activePeriod != null) {
                result.put(empId, new EmploymentSummary(
                        EmployeeEmploymentStatusFilter.ACTIVE,
                        latestJobTitleByPeriodId.get(activePeriod.getEmploymentId())
                ));
            } else {
                result.put(empId, new EmploymentSummary(
                        EmployeeEmploymentStatusFilter.TERMINATED,
                        null
                ));
            }
        }

        return result;
    }

    private EmployeeDetails toEmployeeDetails(EmployeeDetailsProjection projection, EmploymentSummary summary) {
        return new EmployeeDetails(
                projection.getId(),
                projection.getFirstName(),
                projection.getLastName(),
                projection.getEmail(),
                projection.getPhoneNumber(),
                projection.getGender(),
                projection.getBirthDate(),
                projection.getAddressLine(),
                projection.getDistrictId(),
                projection.getDui(),
                projection.getNit(),
                projection.getIsss(),
                projection.getNup(),
                projection.getPensionFundInstitution(),
                projection.getEmergencyContactName(),
                projection.getEmergencyContactPhone(),
                summary.status(),
                summary.currentJobTitle(),
                projection.getCreatedAt()
        );
    }

    private record EmploymentSummary(EmployeeEmploymentStatusFilter status, String currentJobTitle) {
        private static final EmploymentSummary EMPTY = new EmploymentSummary(
                EmployeeEmploymentStatusFilter.WITHOUT_PERIOD,
                null
        );
    }
}
