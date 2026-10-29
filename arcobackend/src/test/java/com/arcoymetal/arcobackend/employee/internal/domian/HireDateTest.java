package com.arcoymetal.arcobackend.employee.internal.domian;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

public class HireDateTest {

    private static final ZoneId EL_SALVADOR = ZoneId.of("America/El_Salvador");

    @Test
    void shouldCreateHireDateWithValueIsValid() {
        LocalDate date = LocalDate.of(1998, 5, 11);
        HireDate hireDate = new HireDate(date);

        assertThat(hireDate.value()).isEqualTo(date);
    }

    @Test
    void shouldAcceptTodayAsHireDate() {
        LocalDate today = LocalDate.now(EL_SALVADOR);
        HireDate hireDate = new HireDate(today);
        assertThat(hireDate.value()).isEqualTo(today);
    }

    @Test
    void shouldAcceptPastDateAsHireDate() {
        LocalDate yesterday = LocalDate.now(EL_SALVADOR).minusDays(1);
        HireDate hireDate = new HireDate(yesterday);
        assertThat(hireDate.value()).isEqualTo(yesterday);
    }

    @Test
    void shouldThrowExceptionWhenValueIsInTheFuture() {
        LocalDate tomorrow = LocalDate.now(EL_SALVADOR).plusDays(1);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new HireDate(tomorrow))
                .withMessage("Hire date cannot be in the future");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatNullPointerException()
                .isThrownBy(() -> new HireDate(null))
                .withMessage("Hire date cannot be null");
    }

}