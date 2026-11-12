package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.adapter;


import org.springframework.stereotype.Repository;

import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeRepository;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Employee;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmployeeEntity;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.mapper.EmployeePersistenceMapper;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository.SpringDataEmployeeRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor 
public class EmployeePersistenceAdapter  implements EmployeeRepository {

    private final SpringDataEmployeeRepository springDataEmployeeRepository;
    private final EmployeePersistenceMapper employeePersistenceMapper;


    @Override
    public Employee save(Employee employee) {
       EmployeeEntity employeeEntity = employeePersistenceMapper.toEntity(employee);
       return employeePersistenceMapper.toDomain(springDataEmployeeRepository.save(employeeEntity));
    }


    @Override
    public boolean existsByEmail(String email) {
        return springDataEmployeeRepository.existsByEmail(email);
    }


    @Override
    public boolean existsByDui(String dui) {
        return springDataEmployeeRepository.existsByDui(dui);
    }


}
