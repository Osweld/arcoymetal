package com.arcoymetal.arcobackend.employee.internal.application.port.out;

import java.util.List;
import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmploymentPeriodDetails;

public interface EmploymentPeriodQueryRepository {

    List<EmploymentPeriodDetails> findByEmployeeIdOrderByStartDateDesc(UUID employeeId);
}
