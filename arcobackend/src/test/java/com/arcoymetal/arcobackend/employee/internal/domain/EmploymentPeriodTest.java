package com.arcoymetal.arcobackend.employee.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.EmployeeId;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.BaseSalary;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.ContractType;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EffectiveFrom;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentChange;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentChangeId;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentId;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentPeriod;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentStatus;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EndDate;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.JobTitle;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.StartDate;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.TerminationReason;

class EmploymentPeriodTest {

    @Test
    void shouldCreateActiveEmploymentPeriodWithImmutableChanges() {
        EmploymentId employmentId = EmploymentId.generate();
        EmployeeId employeeId = EmployeeId.generate();
        StartDate startDate = new StartDate(LocalDate.of(2026, 2, 1));
        EmploymentChange initialChange = EmploymentChange.create(
                EmploymentChangeId.generate(),
                employmentId,
                new JobTitle("Armador de Estructuras"),
                ContractType.FULL_TIME,
                new BaseSalary(new BigDecimal("550.00")),
                new EffectiveFrom(LocalDate.of(2026, 2, 1))
        );

        List<EmploymentChange> mutableInput = new ArrayList<>();
        mutableInput.add(initialChange);

        EmploymentPeriod period = EmploymentPeriod.create(employmentId, employeeId, startDate, mutableInput);
        mutableInput.clear();

        assertThat(period.getStatus()).isEqualTo(EmploymentStatus.ACTIVE);
        assertThat(period.getEndDate()).isNull();
        assertThat(period.getTerminationReason()).isNull();
        assertThat(period.getEmploymentChanges()).hasSize(1);
        assertThatThrownBy(() -> period.getEmploymentChanges().add(initialChange))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldRejectEmptyChangesOnCreate() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> EmploymentPeriod.create(
                        EmploymentId.generate(),
                        EmployeeId.generate(),
                        new StartDate(LocalDate.of(2026, 2, 1)),
                        List.of()
                ))
                .withMessage("EmploymentPeriod must have at least one initial EmploymentChange");
    }

    @Test
    void shouldRejectChangeBelongingToAnotherEmploymentPeriod() {
        EmploymentId employmentId = EmploymentId.generate();
        EmploymentId otherEmploymentId = EmploymentId.generate();
        EmploymentChange foreignChange = EmploymentChange.create(
                EmploymentChangeId.generate(),
                otherEmploymentId,
                new JobTitle("Soldador"),
                ContractType.FULL_TIME,
                new BaseSalary(new BigDecimal("500.00")),
                new EffectiveFrom(LocalDate.of(2026, 2, 1))
        );

        assertThatIllegalArgumentException()
                .isThrownBy(() -> EmploymentPeriod.create(
                        employmentId,
                        EmployeeId.generate(),
                        new StartDate(LocalDate.of(2026, 2, 1)),
                        List.of(foreignChange)
                ))
                .withMessage("EmploymentChange must belong to the same EmploymentPeriod");
    }

    @Test
    void shouldRejectDuplicateEffectiveFromDates() {
        EmploymentId employmentId = EmploymentId.generate();
        EffectiveFrom sameDate = new EffectiveFrom(LocalDate.of(2026, 2, 1));
        EmploymentChange first = EmploymentChange.create(
                EmploymentChangeId.generate(),
                employmentId,
                new JobTitle("Soldador I"),
                ContractType.FULL_TIME,
                new BaseSalary(new BigDecimal("500.00")),
                sameDate
        );
        EmploymentChange second = EmploymentChange.create(
                EmploymentChangeId.generate(),
                employmentId,
                new JobTitle("Soldador II"),
                ContractType.FULL_TIME,
                new BaseSalary(new BigDecimal("600.00")),
                sameDate
        );

        assertThatIllegalArgumentException()
                .isThrownBy(() -> EmploymentPeriod.create(
                        employmentId,
                        EmployeeId.generate(),
                        new StartDate(LocalDate.of(2026, 2, 1)),
                        List.of(first, second)
                ))
                .withMessageContaining("Duplicate effectiveFrom date");
    }

    @Test
    void shouldReconstituteTerminatedEmploymentPeriodAndValidateConsistency() {
        EmploymentId employmentId = EmploymentId.generate();
        EmployeeId employeeId = EmployeeId.generate();
        StartDate startDate = new StartDate(LocalDate.of(2025, 1, 1));
        EndDate endDate = new EndDate(LocalDate.of(2026, 1, 1));
        EmploymentChange change = EmploymentChange.create(
                EmploymentChangeId.generate(),
                employmentId,
                new JobTitle("Supervisor"),
                ContractType.FULL_TIME,
                new BaseSalary(new BigDecimal("900.00")),
                new EffectiveFrom(LocalDate.of(2025, 1, 1))
        );

        EmploymentPeriod terminated = EmploymentPeriod.reconstitute(
                employmentId,
                employeeId,
                startDate,
                endDate,
                EmploymentStatus.TERMINATED,
                TerminationReason.RESIGNATION,
                List.of(change)
        );
        assertThat(terminated.getStatus()).isEqualTo(EmploymentStatus.TERMINATED);
        assertThat(terminated.getEndDate()).isEqualTo(endDate);
        assertThat(terminated.getTerminationReason()).isEqualTo(TerminationReason.RESIGNATION);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> EmploymentPeriod.reconstitute(
                        employmentId,
                        employeeId,
                        startDate,
                        endDate,
                        EmploymentStatus.ACTIVE,
                        null,
                        List.of(change)
                ))
                .withMessage("Active employment period cannot have endDate or terminationReason");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> EmploymentPeriod.reconstitute(
                        employmentId,
                        employeeId,
                        startDate,
                        null,
                        EmploymentStatus.TERMINATED,
                        TerminationReason.RESIGNATION,
                        List.of(change)
                ))
                .withMessage("Terminated employment period must have endDate and terminationReason");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> EmploymentPeriod.reconstitute(
                        employmentId,
                        employeeId,
                        startDate,
                        new EndDate(LocalDate.of(2024, 12, 31)),
                        EmploymentStatus.TERMINATED,
                        TerminationReason.DISMISSAL,
                        List.of(change)
                ))
                .withMessage("End date cannot be before start date");
    }
}
