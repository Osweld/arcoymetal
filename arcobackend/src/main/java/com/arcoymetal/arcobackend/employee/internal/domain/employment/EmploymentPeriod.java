package com.arcoymetal.arcobackend.employee.internal.domain.employment;

import java.util.Objects;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.EmployeeId;

public class EmploymentPeriod {
    private final EmploymentId employmentId;
    private final EmployeeId employeeId;
    private StartDate startDate;
    private EndDate endDate;
    private JobTitle jobTitle;
    private ContractType contractType;
    private BaseSalary baseSalary;
    private EmployeeStatus status;
    private TerminationReason terminationReason;


    private EmploymentPeriod(EmploymentId employmentId, EmployeeId employeeId, StartDate startDate, EndDate endDate, JobTitle jobTitle, ContractType contractType, BaseSalary baseSalary, EmployeeStatus status, TerminationReason terminationReason) {
        this.employmentId = Objects.requireNonNull(employmentId, "Employment ID cannot be null");
        this.employeeId = Objects.requireNonNull(employeeId, "Employee ID cannot be null");
        this.startDate = Objects.requireNonNull(startDate, "Start date cannot be null");
        this.endDate = endDate;
        this.jobTitle = Objects.requireNonNull(jobTitle, "Job title cannot be null");
        this.contractType = Objects.requireNonNull(contractType, "Contract type cannot be null");
        this.baseSalary = Objects.requireNonNull(baseSalary, "Base salary cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.terminationReason = terminationReason;
    }

    public EmploymentPeriod createEmploymentPeriod(EmploymentId employmentId, EmployeeId employeeId, StartDate startDate, JobTitle jobTitle, ContractType contractType, BaseSalary baseSalary) {
        return new EmploymentPeriod(employmentId, employeeId, startDate, null, jobTitle, contractType, baseSalary, EmployeeStatus.ACTIVE, null);
    }

    public EmploymentPeriod reconstitute(EmploymentId employmentId, EmployeeId employeeId, StartDate startDate, EndDate endDate, JobTitle jobTitle, ContractType contractType, BaseSalary baseSalary, EmployeeStatus status, TerminationReason terminationReason) {
        return new EmploymentPeriod(employmentId, employeeId, startDate, endDate, jobTitle, contractType, baseSalary, status, terminationReason);
    }

    public EmploymentId getEmploymentId() {
        return employmentId;
    }

    public EmployeeId getEmployeeId() {
        return employeeId;
    }

    public StartDate getStartDate() {
        return startDate;
    }

    public EndDate getEndDate() {
        return endDate;
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

    public EmployeeStatus getStatus() {
        return status;
    }

    public TerminationReason getTerminationReason() {
        return terminationReason;
    }

    

}
