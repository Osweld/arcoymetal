package com.arcoymetal.arcobackend.employee.internal.application.port.out;


import com.arcoymetal.arcobackend.employee.internal.domian.Employee;

public interface EmployeeRepository {

    void save(Employee employee);

}
