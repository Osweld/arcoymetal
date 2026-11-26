package com.arcoymetal.arcobackend.employee.internal.application.port.out;

import java.time.LocalDate;
import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentChange;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentPeriod;

public interface EmploymentPeriodRepository {

    void save(EmploymentPeriod employmentPeriod);

    void saveChange(EmploymentChange employmentChange);

    void deleteChange(UUID employmentChangeId);

    EmploymentPeriod findById(UUID employmentId);

    boolean existsActiveByEmployeeId(UUID employeeId);

    boolean existsTerminatedEndingOnOrAfter(UUID employeeId, LocalDate startDate);

}
