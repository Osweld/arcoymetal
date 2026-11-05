package com.arcoymetal.arcobackend.location.internal.infrastructure.persistence.adapter;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.arcoymetal.arcobackend.location.internal.application.dto.DepartmentDetails;
import com.arcoymetal.arcobackend.location.internal.application.port.out.LocationQueryRepository;
import com.arcoymetal.arcobackend.location.internal.infrastructure.persistence.mapper.LocationResponseMapper;
import com.arcoymetal.arcobackend.location.internal.infrastructure.persistence.repository.SpringDataDepartmentRepository;

import lombok.RequiredArgsConstructor;

@Repository 
@RequiredArgsConstructor 
public class LocationQueryRepositoryAdapter implements LocationQueryRepository {

    private final SpringDataDepartmentRepository springDataDepartmentRepository;
    private final LocationResponseMapper locationResponseMapper;

    @Override
    public List<DepartmentDetails> findAllHierarchy() {
        return springDataDepartmentRepository.findAll().stream()
            .map(locationResponseMapper::toDepartmentDetails)
            .toList();
    }

}
