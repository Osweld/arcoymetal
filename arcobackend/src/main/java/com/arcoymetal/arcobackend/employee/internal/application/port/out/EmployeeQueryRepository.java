package com.arcoymetal.arcobackend.employee.internal.application.port.out;

import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeDetails;
import com.arcoymetal.arcobackend.employee.internal.application.dto.PageQuery;
import com.arcoymetal.arcobackend.employee.internal.application.dto.PageResult;

public interface EmployeeQueryRepository {

    EmployeeDetails findById(UUID employeeId);
    PageResult<EmployeeDetails> findAllWithPagination(PageQuery pageQuery);

}
