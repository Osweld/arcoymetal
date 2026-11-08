package com.arcoymetal.arcobackend.employee.internal.infrastructure.api;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.arcoymetal.arcobackend.employee.api.EmployeeExistenceVerifier;
import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository.SpringDataEmployeeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor 
public class EmployeeExistenceVerifierImpl implements EmployeeExistenceVerifier {

    private final SpringDataEmployeeRepository employeeRepository;

    @Override
    public boolean existsByEmployeeId(UUID employeeId) {
        return employeeRepository.existsById(employeeId);
    }

}
