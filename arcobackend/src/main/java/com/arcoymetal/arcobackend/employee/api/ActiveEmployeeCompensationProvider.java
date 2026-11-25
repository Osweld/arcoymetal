package com.arcoymetal.arcobackend.employee.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActiveEmployeeCompensationProvider {

    List<EmployeeCompensationSnapshot> findActiveCompensationsAt(LocalDate referenceDate);

    Optional<EmployeeCompensationSnapshot> findCompensationAt(UUID employeeId, LocalDate referenceDate);
}
