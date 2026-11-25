package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.adapter;


import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeRepository;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.Employee;
import com.arcoymetal.arcobackend.employee.internal.domain.exception.EmployeeNotFoundException;
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

    @Override
    public boolean existsByNit(String nit) {
        return springDataEmployeeRepository.existsByNit(nit);
    }

    @Override
    public boolean existsByIsss(String isss) {
        return springDataEmployeeRepository.existsByIsss(isss);
    }

    @Override
    public boolean existsByNup(String nup) {
        return springDataEmployeeRepository.existsByNup(nup);
    }


    @Override
    public Employee findById(UUID employeeId) {
        EmployeeEntity employeeEntity = springDataEmployeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        return employeePersistenceMapper.toDomain(employeeEntity);
    }


    @Override
    public boolean existsById(UUID employeeId) {
        return springDataEmployeeRepository.existsById(employeeId);
    }


}
