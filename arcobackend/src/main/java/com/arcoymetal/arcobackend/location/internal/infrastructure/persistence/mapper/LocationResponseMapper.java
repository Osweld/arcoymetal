package com.arcoymetal.arcobackend.location.internal.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.arcoymetal.arcobackend.location.internal.application.dto.DepartmentDetails;
import com.arcoymetal.arcobackend.location.internal.application.dto.DistrictDetails;
import com.arcoymetal.arcobackend.location.internal.application.dto.MunicipalityDetails;
import com.arcoymetal.arcobackend.location.internal.infrastructure.persistence.entity.DepartmentEntity;
import com.arcoymetal.arcobackend.location.internal.infrastructure.persistence.entity.DistrictEntity;
import com.arcoymetal.arcobackend.location.internal.infrastructure.persistence.entity.MunicipalityEntity;

@Component
public class LocationResponseMapper {

    public DepartmentDetails toDepartmentDetails(DepartmentEntity departmentEntity) {
        return new DepartmentDetails(
            departmentEntity.getId(),
            departmentEntity.getName(),
            departmentEntity.getMunicipalities().stream()
                .map(this::toMunicipalityDetails)
                .toList()
        );
    }

    private MunicipalityDetails toMunicipalityDetails(MunicipalityEntity municipalityEntity) {
        return new MunicipalityDetails(
            municipalityEntity.getId(),
            municipalityEntity.getName(),
            municipalityEntity.getDistricts().stream()
                .map(this::toDistrictDetails)
                .toList()
        );
    }

    private DistrictDetails toDistrictDetails(DistrictEntity districtEntity) {
        return new DistrictDetails(
            districtEntity.getId(),
            districtEntity.getName()
        );
    }

}
