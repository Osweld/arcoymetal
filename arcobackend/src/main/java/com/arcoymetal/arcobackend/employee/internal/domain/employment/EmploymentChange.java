package com.arcoymetal.arcobackend.employee.internal.domain.employment;

import java.util.Objects;

public class EmploymentChange {

    private final EmploymentChangeId employmentChangeId;
    private final EmploymentId employmentId;
    private final JobTitle jobTitle;
    private final ContractType contractType;
    private final BaseSalary baseSalary;
    private final EffectiveFrom effectiveFrom;

private EmploymentChange(
            EmploymentChangeId employmentChangeId,
            EmploymentId employmentId,
            JobTitle jobTitle,
            ContractType contractType,
            BaseSalary baseSalary,
            EffectiveFrom effectiveFrom
    ) {
        this.employmentChangeId = Objects.requireNonNull(employmentChangeId, "EmploymentChangeId cannot be null");
        this.employmentId = Objects.requireNonNull(employmentId, "EmploymentId cannot be null");
        this.jobTitle = Objects.requireNonNull(jobTitle, "JobTitle cannot be null");
        this.contractType = Objects.requireNonNull(contractType, "ContractType cannot be null");
        this.baseSalary = Objects.requireNonNull(baseSalary, "BaseSalary cannot be null");
        this.effectiveFrom = Objects.requireNonNull(effectiveFrom, "EffectiveFrom cannot be null");
    }

    public static EmploymentChange create(
            EmploymentChangeId employmentChangeId,
            EmploymentId employmentId,
            JobTitle jobTitle,
            ContractType contractType,
            BaseSalary baseSalary,
            EffectiveFrom effectiveFrom
    ) {
        return new EmploymentChange(employmentChangeId, employmentId, jobTitle, contractType, baseSalary, effectiveFrom);
    }

    public static EmploymentChange reconstitute(
            EmploymentChangeId employmentChangeId,
            EmploymentId employmentId,
            JobTitle jobTitle,
            ContractType contractType,
            BaseSalary baseSalary,
            EffectiveFrom effectiveFrom
    ) {
        return new EmploymentChange(employmentChangeId, employmentId, jobTitle, contractType, baseSalary, effectiveFrom);
    }





    public EmploymentChangeId getEmploymentChangeId() {
        return employmentChangeId;
    }

    public EmploymentId getEmploymentId() {
        return employmentId;
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
