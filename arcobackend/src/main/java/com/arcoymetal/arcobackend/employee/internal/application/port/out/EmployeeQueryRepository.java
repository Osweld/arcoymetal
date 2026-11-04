package com.arcoymetal.arcobackend.employee.internal.application.port.out;

import java.util.List;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeDetails;

public interface EmployeeQueryRepository {

    EmployeeDetails findEmployeeDetailsById(String employeeId);
    List<EmployeeDetails> findAllEmployeeDetails();

}
