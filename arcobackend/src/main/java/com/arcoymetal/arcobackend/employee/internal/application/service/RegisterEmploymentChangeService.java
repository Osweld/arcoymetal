package com.arcoymetal.arcobackend.employee.internal.application.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.employee.internal.application.command.RegisterEmploymentChangeCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.RegisterEmploymentChangeUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmploymentPeriodRepository;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.BaseSalary;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EffectiveFrom;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentChange;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentChangeId;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentPeriod;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.JobTitle;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterEmploymentChangeService implements RegisterEmploymentChangeUseCase {

    private final EmploymentPeriodRepository employmentPeriodRepository;

    @Override
    @Transactional
    public EmploymentChangeId registerChange(RegisterEmploymentChangeCommand command) {
        Objects.requireNonNull(command, "RegisterEmploymentChangeCommand cannot be null");

        EmploymentPeriod existingPeriod = employmentPeriodRepository.findById(command.employmentPeriodId());
        EmploymentChangeId changeId = EmploymentChangeId.generate();

        EmploymentChange newChange = EmploymentChange.create(
                changeId,
                existingPeriod.getEmploymentId(),
                new JobTitle(command.jobTitle()),
                command.contractType(),
                new BaseSalary(command.baseSalary()),
                new EffectiveFrom(command.effectiveFrom())
        );

        // Domain invariant validation (ACTIVE, effectiveFrom >= startDate, no duplicate effectiveFrom)
        existingPeriod.registerChange(newChange);

        // Append-only persistence to preserve historical immutability
        employmentPeriodRepository.saveChange(newChange);
        return changeId;
    }
}
