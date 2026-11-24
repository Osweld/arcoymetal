package com.arcoymetal.arcobackend.employee.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.EffectiveFrom;

class EffectiveFromTest {

    @Test
    void shouldCreateEffectiveFromWhenValid() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        assertThat(new EffectiveFrom(date).value()).isEqualTo(date);
    }

    @Test
    void shouldRejectNullValue() {
        assertThatNullPointerException()
                .isThrownBy(() -> new EffectiveFrom(null))
                .withMessage("effectiveFrom cannot be null");
    }
}
