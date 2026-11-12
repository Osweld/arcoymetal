package com.arcoymetal.arcobackend.employee.internal.application.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeDetails;
import com.arcoymetal.arcobackend.employee.internal.application.dto.PageQuery;
import com.arcoymetal.arcobackend.employee.internal.application.dto.PageResult;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.GetAllEmployeeWithPaginationUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeQueryRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class GetAllEmployeeWithPaginationService implements GetAllEmployeeWithPaginationUseCase {

    private final EmployeeQueryRepository employeeQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<EmployeeDetails> getAllEmployees(PageQuery pageQuery) {

        Objects.requireNonNull(pageQuery, "PageQuery cannot be null");


        return employeeQueryRepository.findAllWithPagination(pageQuery);
    }



}
