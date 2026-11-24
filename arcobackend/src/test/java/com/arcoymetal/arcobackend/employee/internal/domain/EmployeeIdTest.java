package com.arcoymetal.arcobackend.employee.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.EmployeeId;

public class EmployeeIdTest {

   @Test
    void shouldCreateEmployeeIdWhenValueIsValid() {
        UUID uuid = UUID.fromString(
            "550e8400-e29b-41d4-a716-446655440000"
        );

        EmployeeId employeeId = new EmployeeId(uuid);

        assertThat(employeeId.value()).isEqualTo(uuid);
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatNullPointerException()
            .isThrownBy(() -> new EmployeeId(null))
            .withMessage("EmployeeId value cannot be null");
    }

    @Test
    void shouldGenerateEmployeeId() {
        EmployeeId employeeId = EmployeeId.generate();

        assertThat(employeeId.value()).isNotNull();
        assertThat(employeeId.value().version()).isEqualTo(4);
        assertThat(employeeId.value().variant()).isEqualTo(2);
    }

    @Test
    void shouldGenerateDifferentIds() {
        EmployeeId firstId = EmployeeId.generate();
        EmployeeId secondId = EmployeeId.generate();

        assertThat(firstId).isNotEqualTo(secondId);
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        UUID uuid = UUID.fromString(
            "550e8400-e29b-41d4-a716-446655440000"
        );

        EmployeeId firstId = new EmployeeId(uuid);
        EmployeeId secondId = new EmployeeId(uuid);

        assertThat(firstId).isEqualTo(secondId);
        assertThat(firstId.hashCode()).hasSameHashCodeAs(secondId.hashCode());
    }
}
