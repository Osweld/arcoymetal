package com.arcoymetal.arcobackend.employee.internal.domain.employment;

import java.util.Objects;

public class EmploymentChange {

    private final EmploymentChangeId id;
    private final JobTitle jobTitle;
    private final ContractType contractType;
    private final BaseSalary baseSalary;
    private final EffectiveFrom effectiveFrom;

public EmploymentChange(
            EmploymentChangeId id,
            JobTitle jobTitle,
            ContractType contractType,
            BaseSalary baseSalary,
            EffectiveFrom effectiveFrom
    ) {
        this.id = Objects.requireNonNull(id, "EmploymentChangeId cannot be null");
        this.jobTitle = Objects.requireNonNull(jobTitle, "JobTitle cannot be null");
        this.contractType = Objects.requireNonNull(contractType, "ContractType cannot be null");
        this.baseSalary = Objects.requireNonNull(baseSalary, "BaseSalary cannot be null");
        this.effectiveFrom = Objects.requireNonNull(effectiveFrom, "EffectiveFrom cannot be null");
    }

    public EmploymentChangeId getId() {
        return id;
    }

    public JobTitle getJobTitle() {
        return jobTitle;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public BaseSalary getBaseSalary() {
        return baseSalary;
    }

    public EffectiveFrom getEffectiveFrom() {
        return effectiveFrom;
    }

}
