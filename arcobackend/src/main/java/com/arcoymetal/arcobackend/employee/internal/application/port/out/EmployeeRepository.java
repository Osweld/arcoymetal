package com.arcoymetal.arcobackend.employee.internal.application.port.out;


import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.Employee;

public interface EmployeeRepository {

    void save(Employee employee);
    boolean existsByEmail(String email);
    boolean existsByDui(String dui);
    void update(Employee employee);
    Employee findById(UUID id);

}
