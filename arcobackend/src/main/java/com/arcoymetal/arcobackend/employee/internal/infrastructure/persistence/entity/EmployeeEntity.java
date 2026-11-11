package com.arcoymetal.arcobackend.employee.internal.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.Gender;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.PensionFundInstitution;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.ContractType;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmployeeStatus;
import com.arcoymetal.arcobackend.shared.infrastructure.persistence.entity.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;


@Entity
@Table(name = "employee")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmployeeEntity extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 8)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Gender gender;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "address_line", nullable = false, length = 150)
    private String addressLine;

    @Column(name = "district_id", nullable = false)
    private String districtId;

    @Column(nullable = false, unique = true, length = 9)
    private String dui;

    @Column(unique = true, length = 17)
    private String nit;

    @Column(unique = true, length = 9)
    private String isss;

    @Column(unique = true, length = 9)
    private String nup;

    @Enumerated(EnumType.STRING)
    @Column(name = "pension_fund_institution", nullable = false, length = 30)
    private PensionFundInstitution pensionFundInstitution;

}