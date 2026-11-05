package com.arcoymetal.arcobackend.location.internal.infrastructure.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcoymetal.arcobackend.location.internal.application.dto.DepartmentDetails;
import com.arcoymetal.arcobackend.location.internal.application.port.in.GetAllDepartmentHierarchyUseCase;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class GetAllDepartmentHierarchyController {

    private final GetAllDepartmentHierarchyUseCase getAllDepartmentHierarchyUseCase;



    @GetMapping("/hierarchy")
    public ResponseEntity<List<DepartmentDetails>> getAllDepartmentHierarchy() {
        return ResponseEntity.ok(getAllDepartmentHierarchyUseCase.findAllHierarchy());
    }
    

}
