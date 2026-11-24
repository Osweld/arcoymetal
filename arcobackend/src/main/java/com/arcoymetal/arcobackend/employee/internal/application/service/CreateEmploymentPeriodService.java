package com.arcoymetal.arcobackend.employee.internal.application.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.employee.internal.application.command.CreateEmploymentPeriodCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.CreateEmploymentPeriodUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmployeeRepository;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmploymentPeriodRepository;
import com.arcoymetal.arcobackend.employee.internal.domain.employee.EmployeeId;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.BaseSalary;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EffectiveFrom;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentChange;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentChangeId;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentId;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentPeriod;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.JobTitle;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.StartDate;
import com.arcoymetal.arcobackend.employee.internal.domain.exception.ActiveEmploymentPeriodAlreadyExistsException;
import com.arcoymetal.arcobackend.employee.internal.domain.exception.EmployeeNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateEmploymentPeriodService implements CreateEmploymentPeriodUseCase {

    private final EmploymentPeriodRepository employmentPeriodRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public EmploymentId createEmploymentPeriod(CreateEmploymentPeriodCommand command) {

        Objects.requireNonNull(command, "CreateEmploymentPeriodCommand cannot be null");

        if (!employeeRepository.existsById(command.employeeId())) {
            throw new EmployeeNotFoundException(command.employeeId());
        }

        if (employmentPeriodRepository.existsActiveByEmployeeId(command.employeeId())) {
            throw new ActiveEmploymentPeriodAlreadyExistsException(command.employeeId());
        }

        EmploymentId employmentId = EmploymentId.generate();
        EmploymentChangeId employmentChangeId = EmploymentChangeId.generate();

        EmploymentChange employmentChange = EmploymentChange.create(
                employmentChangeId,
                employmentId,
                new JobTitle(command.jobTitle()),
                command.contractType(),
                new BaseSalary(command.baseSalary()),
                new EffectiveFrom(command.startDate())
        );

        EmploymentPeriod employmentPeriod = EmploymentPeriod.create(
                employmentId,
                new EmployeeId(command.employeeId()),
                new StartDate(command.startDate()),
                List.of(employmentChange)
        );

        employmentPeriodRepository.save(employmentPeriod);
        return employmentId;
    }

}
