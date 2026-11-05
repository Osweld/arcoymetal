package com.arcoymetal.arcobackend.location.internal.application.port.in;

import java.util.List;

import com.arcoymetal.arcobackend.location.internal.application.dto.DepartmentDetails;

public interface GetAllDepartmentHierarchyUseCase {

    List<DepartmentDetails> findAllHierarchy();

}
