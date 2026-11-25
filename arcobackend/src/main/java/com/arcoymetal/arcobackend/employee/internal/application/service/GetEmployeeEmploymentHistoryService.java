package com.arcoymetal.arcobackend.employee.internal.application.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmploymentPeriodDetails;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.GetEmployeeEmploymentHistoryUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeRepository;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmploymentPeriodQueryRepository;
import com.arcoymetal.arcobackend.employee.internal.application.query.GetEmployeeEmploymentHistoryQuery;
import com.arcoymetal.arcobackend.employee.internal.domain.exception.EmployeeNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetEmployeeEmploymentHistoryService implements GetEmployeeEmploymentHistoryUseCase {

    private final EmployeeRepository employeeRepository;
    private final EmploymentPeriodQueryRepository employmentPeriodQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EmploymentPeriodDetails> getEmploymentHistory(GetEmployeeEmploymentHistoryQuery query) {
        Objects.requireNonNull(query, "GetEmployeeEmploymentHistoryQuery cannot be null");

        if (!employeeRepository.existsById(query.employeeId())) {
            throw new EmployeeNotFoundException(query.employeeId());
        }

        return employmentPeriodQueryRepository.findByEmployeeIdOrderByStartDateDesc(query.employeeId());
    }
}
