package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity.EmploymentChangeEntity;

public interface SpringDataEmploymentChange extends JpaRepository<EmploymentChangeEntity, UUID>{

    Optional<List<EmploymentChangeEntity>> findByEmploymentId(UUID employmentId);

}
