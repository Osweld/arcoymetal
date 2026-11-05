package com.arcoymetal.arcobackend.location.internal.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.arcoymetal.arcobackend.location.internal.application.dto.DepartmentDetails;
import com.arcoymetal.arcobackend.location.internal.application.port.in.GetAllDepartmentHierarchyUseCase;
import com.arcoymetal.arcobackend.location.internal.application.port.out.LocationQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class GetAllDepartmentHierarchyService implements GetAllDepartmentHierarchyUseCase {

    private final LocationQueryRepository locationQueryRepository;



    @Override
    public List<DepartmentDetails> findAllHierarchy() {
        return locationQueryRepository.findAllHierarchy();
    }


}
