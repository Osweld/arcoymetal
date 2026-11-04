package com.arcoymetal.arcobackend.employee.internal.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeDetails;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.GetAllEmployeeUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeQueryRepository;

@Service 
public class GetAllEmployeeService implements GetAllEmployeeUseCase {

    private final EmployeeQueryRepository employeeQueryRepository;

    public GetAllEmployeeService(EmployeeQueryRepository employeeQueryRepository) {
        this.employeeQueryRepository = employeeQueryRepository;
    }

    @Override
    public List<EmployeeDetails> getAllEmployees() {
        return employeeQueryRepository.findAllEmployeeDetails();
    }

}
