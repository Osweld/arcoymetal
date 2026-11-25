package com.arcoymetal.arcobackend.employee.internal.application.service;

import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arcoymetal.arcobackend.employee.api.EmploymentPeriodTerminatedEvent;
import com.arcoymetal.arcobackend.employee.internal.application.command.TerminateEmploymentPeriodCommand;
import com.arcoymetal.arcobackend.employee.internal.application.port.in.TerminateEmploymentPeriodUseCase;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmploymentPeriodRepository;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentId;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentPeriod;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EndDate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TerminateEmploymentPeriodService implements TerminateEmploymentPeriodUseCase {

    private final EmploymentPeriodRepository employmentPeriodRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public EmploymentId terminateEmploymentPeriod(TerminateEmploymentPeriodCommand command) {
        Objects.requireNonNull(command, "TerminateEmploymentPeriodCommand cannot be null");

        EmploymentPeriod existingPeriod = employmentPeriodRepository.findById(command.employmentPeriodId());
        EmploymentPeriod terminatedPeriod = existingPeriod.terminate(
                new EndDate(command.endDate()),
                command.terminationReason()
        );

        employmentPeriodRepository.save(terminatedPeriod);
        eventPublisher.publishEvent(new EmploymentPeriodTerminatedEvent(
                terminatedPeriod.getEmployeeId().value(),
                terminatedPeriod.getEmploymentId().value(),
                terminatedPeriod.getEndDate().value()
        ));

        return terminatedPeriod.getEmploymentId();
    }
}
