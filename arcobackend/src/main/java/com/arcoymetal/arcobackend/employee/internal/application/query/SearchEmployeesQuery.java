package com.arcoymetal.arcobackend.employee.internal.application.query;

import java.util.Objects;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeEmploymentStatusFilter;
import com.arcoymetal.arcobackend.employee.internal.application.dto.PageQuery;

public record SearchEmployeesQuery(
        PageQuery pageQuery,
        String search,
        EmployeeEmploymentStatusFilter employmentStatus
) {
    public SearchEmployeesQuery {
        Objects.requireNonNull(pageQuery, "PageQuery cannot be null");
        search = (search != null && !search.isBlank()) ? search.strip() : null;
    }
}
