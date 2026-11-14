package com.arcoymetal.arcobackend.employee.internal.application.port.out;


import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.Employee;

public interface EmployeeRepository {

    Employee save(Employee employee);
    boolean existsByEmail(String email);
    boolean existsByDui(String dui);
    boolean existsById(UUID employeeId);
    Employee findById(UUID employeeId);
}
