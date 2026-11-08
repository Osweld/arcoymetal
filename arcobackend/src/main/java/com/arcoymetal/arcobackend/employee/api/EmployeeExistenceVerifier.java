package com.arcoymetal.arcobackend.employee.api;

import java.util.UUID;

public interface EmployeeExistenceVerifier {

    boolean existsByEmployeeId(UUID employeeId);

}
