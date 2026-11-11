package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.adapter;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeRepository;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Employee;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.exception.EmployeeNotFoundException;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.mapper.EmployeePersistenceMapper;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository.SpringDataEmployeeRepository;

@Repository 
public class EmployeePersistenceAdapter  implements EmployeeRepository {

    private final SpringDataEmployeeRepository springDataEmployeeRepository;
    private final EmployeePersistenceMapper employeePersistenceMapper;

    public EmployeePersistenceAdapter(SpringDataEmployeeRepository springDataEmployeeRepository, EmployeePersistenceMapper employeePersistenceMapper) {
        this.springDataEmployeeRepository = springDataEmployeeRepository;
        this.employeePersistenceMapper = employeePersistenceMapper;
    }


}
