package com.arcoymetal.arcobackend.employee.internal.application.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.employee.internal.application.command.RemoveEmploymentChangeCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.RemoveEmploymentChangeUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmploymentPeriodRepository;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentChangeId;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentPeriod;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RemoveEmploymentChangeService implements RemoveEmploymentChangeUseCase {

    private final EmploymentPeriodRepository employmentPeriodRepository;

    @Override
    @Transactional
    public void removeChange(RemoveEmploymentChangeCommand command) {
        Objects.requireNonNull(command, "RemoveEmploymentChangeCommand cannot be null");

        EmploymentPeriod period = employmentPeriodRepository.findById(command.employmentPeriodId());
        period.removeLatestChange(new EmploymentChangeId(command.employmentChangeId()));
        employmentPeriodRepository.deleteChange(command.employmentChangeId());
    }
}
