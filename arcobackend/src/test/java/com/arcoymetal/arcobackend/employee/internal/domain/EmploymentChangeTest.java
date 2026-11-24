package com.arcoymetal.arcobackend.employee.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.BaseSalary;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.ContractType;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EffectiveFrom;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentChange;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentChangeId;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentId;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.JobTitle;

class EmploymentChangeTest {

    @Test
    void shouldCreateAndReconstituteEmploymentChange() {
        EmploymentChangeId changeId = EmploymentChangeId.generate();
        EmploymentId employmentId = EmploymentId.generate();
        EffectiveFrom effectiveFrom = new EffectiveFrom(LocalDate.of(2026, 1, 1));

        EmploymentChange created = EmploymentChange.create(
                changeId,
                employmentId,
                new JobTitle("Soldador Industrial"),
                ContractType.FULL_TIME,
                new BaseSalary(new BigDecimal("650.00")),
                effectiveFrom
        );

        EmploymentChange reconstituted = EmploymentChange.reconstitute(
                changeId,
                employmentId,
                created.getJobTitle(),
                created.getContractType(),
                created.getBaseSalary(),
                effectiveFrom
        );

        assertThat(created.getEmploymentChangeId()).isEqualTo(changeId);
        assertThat(created.getEmploymentId()).isEqualTo(employmentId);
        assertThat(created.getJobTitle().value()).isEqualTo("Soldador Industrial");
        assertThat(created.getContractType()).isEqualTo(ContractType.FULL_TIME);
        assertThat(created.getBaseSalary().value()).isEqualByComparingTo("650.00");
        assertThat(reconstituted.getEffectiveFrom()).isEqualTo(effectiveFrom);
    }

    @Test
    void shouldRejectNullRequiredFields() {
        EmploymentChangeId changeId = EmploymentChangeId.generate();
        EmploymentId employmentId = EmploymentId.generate();

        assertThatNullPointerException()
                .isThrownBy(() -> EmploymentChange.create(
                        null,
                        employmentId,
                        new JobTitle("Operario"),
                        ContractType.FULL_TIME,
                        new BaseSalary(new BigDecimal("400.00")),
                        new EffectiveFrom(LocalDate.of(2026, 1, 1))
                ))
                .withMessage("EmploymentChangeId cannot be null");

        assertThatNullPointerException()
                .isThrownBy(() -> EmploymentChange.create(
                        changeId,
                        employmentId,
                        new JobTitle("Operario"),
                        ContractType.FULL_TIME,
                        null,
                        new EffectiveFrom(LocalDate.of(2026, 1, 1))
                ))
                .withMessage("BaseSalary cannot be null");
    }
}
