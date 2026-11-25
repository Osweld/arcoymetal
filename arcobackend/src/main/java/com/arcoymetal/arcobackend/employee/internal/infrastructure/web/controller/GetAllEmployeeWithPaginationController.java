package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeDetails;
import com.arcoymetal.arcobackend.employee.internal.application.dto.EmployeeEmploymentStatusFilter;
import com.arcoymetal.arcobackend.employee.internal.application.dto.PageQuery;
import com.arcoymetal.arcobackend.employee.internal.application.dto.PageResult;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.GetAllEmployeeWithPaginationUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.query.SearchEmployeesQuery;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class GetAllEmployeeWithPaginationController {

    private final GetAllEmployeeWithPaginationUseCase getAllEmployeeWithPaginationUseCase;

    @GetMapping
    public ResponseEntity<PageResult<EmployeeDetails>> getAllEmployeesWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) EmployeeEmploymentStatusFilter employmentStatus
    ) {
        SearchEmployeesQuery query = new SearchEmployeesQuery(new PageQuery(page, size), search, employmentStatus);
        PageResult<EmployeeDetails> result = getAllEmployeeWithPaginationUseCase.getAllEmployees(query);
        return ResponseEntity.ok(result);
    }
}
