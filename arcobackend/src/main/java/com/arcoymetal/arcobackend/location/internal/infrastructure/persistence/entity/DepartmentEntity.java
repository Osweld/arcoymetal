package com.arcoymetal.arcobackend.location.internal.infrastructure.persistence.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name = "department")
@Getter 
@Setter 
@NoArgsConstructor 
public class DepartmentEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "department")
    private Set<MunicipalityEntity> municipalities = new HashSet<>();

}
