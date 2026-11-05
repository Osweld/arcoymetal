package com.arcoymetal.arcobackend.location.internal.application.port.out;

import java.util.List;

import com.arcoymetal.arcobackend.location.internal.application.dto.DepartmentDetails;

public interface LocationQueryRepository {

    List<DepartmentDetails> findAllHierarchy();

}
