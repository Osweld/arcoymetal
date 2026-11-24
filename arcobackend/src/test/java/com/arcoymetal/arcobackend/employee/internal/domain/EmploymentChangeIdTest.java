package com.arcoymetal.arcobackend.employee.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentChangeId;

class EmploymentChangeIdTest {

    @Test
    void shouldCreateAndGenerateEmploymentChangeId() {
        UUID uuid = UUID.randomUUID();
        assertThat(new EmploymentChangeId(uuid).value()).isEqualTo(uuid);
        assertThat(EmploymentChangeId.generate().value()).isNotNull();
    }

    @Test
    void shouldRejectNullValue() {
        assertThatNullPointerException()
                .isThrownBy(() -> new EmploymentChangeId(null))
                .withMessage("Employment change ID cannot be null");
    }
}
