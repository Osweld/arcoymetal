package com.arcoymetal.arcobackend.employee.internal.application.port.in;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeDetails;
import com.arcoymetal.arcobackend.employee.internal.application.dto.PageResult;
import com.arcoymetal.arcobackend.employee.internal.application.query.SearchEmployeesQuery;

public interface GetAllEmployeeWithPaginationUseCase {

    PageResult<EmployeeDetails> getAllEmployees(SearchEmployeesQuery query);
}
