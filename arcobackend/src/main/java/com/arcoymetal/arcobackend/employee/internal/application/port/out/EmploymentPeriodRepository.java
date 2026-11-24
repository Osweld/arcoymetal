package com.arcoymetal.arcobackend.employee.internal.application.port.out;

import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentPeriod;

public interface EmploymentPeriodRepository {

    void save(EmploymentPeriod employmentPeriod);

    EmploymentPeriod findById(UUID employmentId);

    boolean existsActiveByEmployeeId(UUID employeeId);

}
