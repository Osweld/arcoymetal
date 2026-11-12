package com.arcoymetal.arcobackend.employee.internal.application.port.in;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeDetails;
import com.arcoymetal.arcobackend.employee.internal.application.dto.PageQuery;
import com.arcoymetal.arcobackend.employee.internal.application.dto.PageResult;

public interface GetAllEmployeeWithPaginationUseCase {

    PageResult<EmployeeDetails> getAllEmployees(PageQuery pageQuery);

}
