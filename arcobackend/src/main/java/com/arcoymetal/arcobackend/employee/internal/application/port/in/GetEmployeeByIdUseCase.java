package com.arcoymetal.arcobackend.employee.internal.application.port.in;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeDetails;
import com.arcoymetal.arcobackend.employee.internal.application.query.GetEmployeeByIdQuery;

public interface GetEmployeeByIdUseCase {

    EmployeeDetails getEmployeeById(GetEmployeeByIdQuery query);

}
