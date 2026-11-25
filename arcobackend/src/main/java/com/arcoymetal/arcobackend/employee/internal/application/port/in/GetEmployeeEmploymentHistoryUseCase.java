package com.arcoymetal.arcobackend.employee.internal.application.port.in;

import java.util.List;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmploymentPeriodDetails;
import com.arcoymetal.arcobackend.employee.internal.application.query.GetEmployeeEmploymentHistoryQuery;

public interface GetEmployeeEmploymentHistoryUseCase {

    List<EmploymentPeriodDetails> getEmploymentHistory(GetEmployeeEmploymentHistoryQuery query);
}
