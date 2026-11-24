package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.adapter;


import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeDetails;
import com.arcoymetal.arcobackend.employee.internal.application.dto.PageQuery;
import com.arcoymetal.arcobackend.employee.internal.application.dto.PageResult;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeQueryRepository;
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
    public PageResult<EmployeeDetails> findAllWithPagination(PageQuery pageQuery) {
        
         Pageable pageable = PageRequest.of(
            pageQuery.page(),
            pageQuery.size(),
            Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<EmployeeDetailsProjection> employeePage = springDataEmployeeRepository.findAllProjectedBy(pageable);

            return new PageResult<EmployeeDetails>(
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
