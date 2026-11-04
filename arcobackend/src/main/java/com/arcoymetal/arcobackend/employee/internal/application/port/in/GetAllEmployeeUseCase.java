package com.arcoymetal.arcobackend.employee.internal.application.port.in;

import java.util.List;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeDetails;

public interface GetAllEmployeeUseCase {

    List<EmployeeDetails> getAllEmployees();

}
