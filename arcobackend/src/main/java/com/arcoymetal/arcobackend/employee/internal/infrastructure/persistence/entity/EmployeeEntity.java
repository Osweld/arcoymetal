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
import lombok.Setter;

@Entity
@Table (name = "employee")
@Getter
@Setter
@NoArgsConstructor()
public class EmployeeEntity extends AuditableEntity{

    @Id 
    private UUID id;
    @Column (name = "first_name", nullable = false)
    private String firstName;
    @Column (name = "last_name", nullable = false)
    private String lastName;
    @Column (name = "email", nullable = false, unique = true)
    private String email;
    @Column (name = "phone_number", nullable = false)
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    @Column (name = "gender", nullable = false)
    private Gender gender;
    @Column (name = "birth_date", nullable = false)
    private LocalDate birthDate;
    @Column (name = "address_line", nullable = false)
    private String addressLine;
    @Column (name = "district_id", nullable = false)
    private String districtId;
    @Column (name = "job_title", nullable = false)
    private String jobTitle;
    @Column (name = "base_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseSalary;
    @Enumerated(EnumType.STRING)
    @Column (name = "contract_type", nullable = false)
    private ContractType contractType;
    @Column (name = "dui", nullable = false, unique = true)
    private String dui;
    @Column (name = "nit", nullable = true, unique = true)
    private String nit;
    @Column (name = "isss", nullable = true, unique = true)
    private String isss;
    @Column (name = "nup", nullable = true, unique = true)
    private String nup;
    @Enumerated(EnumType.STRING)
    @Column (name = "pension_fund_institution", nullable = false)
    private PensionFundInstitution pensionFundInstitution;
    @Column (name = "hire_date", nullable = false)
    private LocalDate hireDate;
    @Column (name = "termination_date", nullable = true)
    private LocalDate terminationDate;
    @Enumerated(EnumType.STRING)
    @Column (name = "status", nullable = false)
    private EmployeeStatus status;
}
