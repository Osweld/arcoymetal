package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.adapter;


import org.springframework.stereotype.Repository;

import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeQueryRepository;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.exception.EmployeeNotFoundException;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository.SpringDataEmployeeRepository;

@Repository
public class EmployeeQueryRepositoryAdapter implements EmployeeQueryRepository {

    private final SpringDataEmployeeRepository springDataEmployeeRepository;

    public EmployeeQueryRepositoryAdapter(SpringDataEmployeeRepository springDataEmployeeRepository) {
        this.springDataEmployeeRepository = springDataEmployeeRepository;
    }

}
