package com.arcoymetal.arcobackend.employee.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.StartDate;

class StartDateTest {

    @Test
    void shouldCreateStartDateWhenValid() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        StartDate startDate = new StartDate(date);
        assertThat(startDate.value()).isEqualTo(date);
    }

    @Test
    void shouldAcceptBoundaryYear1950() {
        LocalDate boundary = LocalDate.of(1950, 1, 1);
        StartDate startDate = new StartDate(boundary);
        assertThat(startDate.value()).isEqualTo(boundary);
    }

    @Test
    void shouldRejectDateBefore1950() {
        LocalDate before1950 = LocalDate.of(1949, 12, 31);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new StartDate(before1950))
                .withMessage("Start date cannot be before 1950");
    }

    @Test
    void shouldRejectNullStartDate() {
        assertThatNullPointerException()
                .isThrownBy(() -> new StartDate(null))
                .withMessage("Start date cannot be null");
    }
}
