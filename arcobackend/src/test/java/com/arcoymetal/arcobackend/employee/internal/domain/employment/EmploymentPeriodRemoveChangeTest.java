package com.arcoymetal.arcobackend.employee.internal.domain.employment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.EmployeeId;
import com.arcoymetal.arcobackend.employee.internal.domain.exception.CannotDeleteInitialEmploymentChangeException;
import com.arcoymetal.arcobackend.employee.internal.domain.exception.EmploymentChangeNotFoundException;
import com.arcoymetal.arcobackend.employee.internal.domain.exception.EmploymentPeriodNotActiveException;
import com.arcoymetal.arcobackend.employee.internal.domain.exception.OnlyLatestEmploymentChangeCanBeRemovedException;

class EmploymentPeriodRemoveChangeTest {

    private EmploymentPeriod createActivePeriodWithInitialChange() {
        EmploymentId periodId = EmploymentId.generate();
        EmploymentChange initial = EmploymentChange.create(
                EmploymentChangeId.generate(),
                periodId,
                new JobTitle("Soldador I"),
                ContractType.FULL_TIME,
                new BaseSalary(new BigDecimal("600.00")),
                new EffectiveFrom(LocalDate.of(2026, 1, 1))
        );
        return EmploymentPeriod.create(
                periodId,
                new EmployeeId(UUID.randomUUID()),
                new StartDate(LocalDate.of(2026, 1, 1)),
                List.of(initial)
        );
    }

    @Test
    void shouldRemoveLatestNonInitialChangeFromActivePeriod() {
        EmploymentPeriod period = createActivePeriodWithInitialChange();
        EmploymentChange second = EmploymentChange.create(
                EmploymentChangeId.generate(),
                period.getEmploymentId(),
                new JobTitle("Soldador II"),
                ContractType.FULL_TIME,
                new BaseSalary(new BigDecimal("720.00")),
                new EffectiveFrom(LocalDate.of(2026, 6, 1))
        );
        EmploymentPeriod withTwoChanges = period.registerChange(second);

        EmploymentPeriod afterRemoval = withTwoChanges.removeLatestChange(second.getEmploymentChangeId());

        assertThat(afterRemoval.getEmploymentChanges()).hasSize(1);
        assertThat(afterRemoval.getEmploymentChanges().getFirst().getJobTitle().value()).isEqualTo("Soldador I");
    }

    @Test
    void shouldRejectRemovingInitialChange() {
        EmploymentPeriod period = createActivePeriodWithInitialChange();
        EmploymentChange initial = period.getEmploymentChanges().getFirst();

        assertThatThrownBy(() -> period.removeLatestChange(initial.getEmploymentChangeId()))
                .isInstanceOf(CannotDeleteInitialEmploymentChangeException.class);
    }

    @Test
    void shouldRejectRemovingIntermediateNonLatestChange() {
        EmploymentPeriod period = createActivePeriodWithInitialChange();
        EmploymentChange middle = EmploymentChange.create(
                EmploymentChangeId.generate(),
                period.getEmploymentId(),
                new JobTitle("Soldador II"),
                ContractType.FULL_TIME,
                new BaseSalary(new BigDecimal("700.00")),
                new EffectiveFrom(LocalDate.of(2026, 4, 1))
        );
        EmploymentChange latest = EmploymentChange.create(
                EmploymentChangeId.generate(),
                period.getEmploymentId(),
                new JobTitle("Soldador Senior"),
                ContractType.FULL_TIME,
                new BaseSalary(new BigDecimal("800.00")),
                new EffectiveFrom(LocalDate.of(2026, 8, 1))
        );
        EmploymentPeriod withThreeChanges = period.registerChange(middle).registerChange(latest);

        assertThatThrownBy(() -> withThreeChanges.removeLatestChange(middle.getEmploymentChangeId()))
                .isInstanceOf(OnlyLatestEmploymentChangeCanBeRemovedException.class);
    }

    @Test
    void shouldRejectRemovingFromTerminatedPeriodAndUnknownChangeId() {
        EmploymentPeriod period = createActivePeriodWithInitialChange();
        EmploymentChange second = EmploymentChange.create(
                EmploymentChangeId.generate(),
                period.getEmploymentId(),
                new JobTitle("Soldador II"),
                ContractType.FULL_TIME,
                new BaseSalary(new BigDecimal("700.00")),
                new EffectiveFrom(LocalDate.of(2026, 4, 1))
        );
        EmploymentPeriod withTwoChanges = period.registerChange(second);

        assertThatThrownBy(() -> withTwoChanges.removeLatestChange(new EmploymentChangeId(UUID.randomUUID())))
                .isInstanceOf(EmploymentChangeNotFoundException.class);

        EmploymentPeriod terminated = withTwoChanges.terminate(
                new EndDate(LocalDate.of(2026, 12, 31)),
                TerminationReason.RESIGNATION
        );
        assertThatThrownBy(() -> terminated.removeLatestChange(second.getEmploymentChangeId()))
                .isInstanceOf(EmploymentPeriodNotActiveException.class);
    }
}
