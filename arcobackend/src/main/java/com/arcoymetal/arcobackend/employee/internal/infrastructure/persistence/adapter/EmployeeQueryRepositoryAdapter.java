package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.adapter;

import java.util.Locale;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeDetails;
import com.arcoymetal.arcobackend.employee.internal.application.dto.PageResult;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeQueryRepository;
import com.arcoymetal.arcobackend.employee.internal.application.query.SearchEmployeesQuery;
import com.arcoymetal.arcobackend.employee.internal.domain.exception.EmployeeNotFoundException;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.projection.EmployeeDetailsProjection;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository.SpringDataEmployeeRepository;

@Repository
public class EmployeeQueryRepositoryAdapter implements EmployeeQueryRepository {

    private final SpringDataEmployeeRepository springDataEmployeeRepository;

    public EmployeeQueryRepositoryAdapter(SpringDataEmployeeRepository springDataEmployeeRepository) {
        this.springDataEmployeeRepository = springDataEmployeeRepository;
    }

    @Override
    public EmployeeDetails findById(UUID employeeId) {
        EmployeeDetailsProjection projection = springDataEmployeeRepository.findProjectedById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        return toEmployeeDetails(projection);
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

        return new PageResult<>(
                employeePage.getContent().stream().map(this::toEmployeeDetails).toList(),
                employeePage.getNumber(),
                employeePage.getSize(),
                employeePage.getTotalElements(),
                employeePage.getTotalPages(),
                employeePage.isFirst(),
                employeePage.isLast()
        );
    }

    private EmployeeDetails toEmployeeDetails(EmployeeDetailsProjection projection) {
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
                projection.getCreatedAt()
        );
    }
}
