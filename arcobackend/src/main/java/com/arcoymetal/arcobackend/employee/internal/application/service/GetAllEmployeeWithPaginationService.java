package com.arcoymetal.arcobackend.employee.internal.application.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeDetails;
import com.arcoymetal.arcobackend.employee.internal.application.dto.PageResult;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.GetAllEmployeeWithPaginationUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeQueryRepository;
import com.arcoymetal.arcobackend.employee.internal.application.query.SearchEmployeesQuery;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAllEmployeeWithPaginationService implements GetAllEmployeeWithPaginationUseCase {

    private final EmployeeQueryRepository employeeQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<EmployeeDetails> getAllEmployees(SearchEmployeesQuery query) {
        Objects.requireNonNull(query, "SearchEmployeesQuery cannot be null");
        return employeeQueryRepository.findAllWithPagination(query);
    }
}
