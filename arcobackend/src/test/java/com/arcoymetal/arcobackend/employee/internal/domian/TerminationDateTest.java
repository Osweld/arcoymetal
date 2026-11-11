package com.arcoymetal.arcobackend.employee.internal.domian;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.EndDate;

public class TerminationDateTest {

     private static final ZoneId EL_SALVADOR = ZoneId.of("America/El_Salvador");

    @Test
    void shouldCreateTerminationDate() {
        LocalDate date = LocalDate.now(EL_SALVADOR).plusDays(1);
        EndDate terminationDate = new EndDate(date);

        assertThat(terminationDate.value()).isEqualTo(date);
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatNullPointerException()
                .isThrownBy(() -> new EndDate(null))
                .withMessage("TerminationDate value cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenValueIsInThePast() {
        LocalDate date = LocalDate.now(EL_SALVADOR).minusDays(1);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new EndDate(date))
                .withMessage("TerminationDate cannot be in the past");
    }

    @Test
    void shouldAcceptTodayAsTerminationDate() {
        LocalDate today = LocalDate.now(EL_SALVADOR);
        EndDate terminationDate = new EndDate(today);
        assertThat(terminationDate.value()).isEqualTo(today);
    }

}
