package com.arcoymetal.arcobackend.employee.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.EndDate;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.StartDate;

class EndDateTest {

    @Test
    void shouldCreateEndDateWhenValid() {
        LocalDate date = LocalDate.of(2026, 5, 31);
        EndDate endDate = new EndDate(date);
        assertThat(endDate.value()).isEqualTo(date);
    }

    @Test
    void shouldRejectNullEndDate() {
        assertThatNullPointerException()
                .isThrownBy(() -> new EndDate(null))
                .withMessage("EndDate value cannot be null");
    }

    @Test
    void shouldCompareAgainstStartDate() {
        StartDate startDate = new StartDate(LocalDate.of(2026, 3, 1));

        assertThat(new EndDate(LocalDate.of(2026, 2, 28)).isBefore(startDate)).isTrue();
        assertThat(new EndDate(LocalDate.of(2026, 3, 1)).isBefore(startDate)).isFalse();
        assertThat(new EndDate(LocalDate.of(2026, 3, 2)).isBefore(startDate)).isFalse();
    }
}
