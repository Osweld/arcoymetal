package com.arcoymetal.arcobackend.location.internal.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.arcoymetal.arcobackend.location.internal.infrastructure.persistence.entity.DepartmentEntity;

public interface SpringDataDepartmentRepository extends JpaRepository<DepartmentEntity, String> {

      @EntityGraph(attributePaths = {
            "municipalities",
            "municipalities.districts"
    })
    List<DepartmentEntity> findAll();

}
