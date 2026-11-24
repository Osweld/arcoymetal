package com.arcoymetal.arcobackend.employee.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentId;

class EmploymentIdTest {

    @Test
    void shouldCreateAndGenerateEmploymentId() {
        UUID uuid = UUID.randomUUID();
        assertThat(new EmploymentId(uuid).value()).isEqualTo(uuid);
        assertThat(EmploymentId.generate().value()).isNotNull();
    }

    @Test
    void shouldRejectNullValue() {
        assertThatNullPointerException()
                .isThrownBy(() -> new EmploymentId(null))
                .withMessage("EmploymentId value cannot be null");
    }
}
