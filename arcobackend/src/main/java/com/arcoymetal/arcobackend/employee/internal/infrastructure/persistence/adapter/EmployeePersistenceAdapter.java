package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.adapter;

import org.springframework.stereotype.Repository;

import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeRepository;
import com.arcoymetal.arcobackend.employee.internal.domian.Employee;
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

    @Override
    public void save(Employee employee) {
        springDataEmployeeRepository.save(employeePersistenceMapper.toEntity(employee));
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
