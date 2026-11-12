package com.arcoymetal.arcobackend.employee.internal.application.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeDetails;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.GetEmployeeByIdUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeQueryRepository;
import com.arcoymetal.arcobackend.employee.internal.application.query.GetEmployeeByIdQuery;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class GetEmployeeByIdService implements GetEmployeeByIdUseCase {

    private final EmployeeQueryRepository employeeQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public EmployeeDetails getEmployeeById(GetEmployeeByIdQuery query) {

        Objects.requireNonNull(query, "GetEmployeeByIdQuery cannot be null");

        return employeeQueryRepository.findById(query.employeeId());
    }

    
}
