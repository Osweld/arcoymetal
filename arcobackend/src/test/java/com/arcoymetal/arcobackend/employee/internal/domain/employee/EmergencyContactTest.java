package com.arcoymetal.arcobackend.employee.internal.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class EmergencyContactTest {

    @Test
    void shouldTrimAndNormalizeValidEmergencyContact() {
        EmergencyContact contact = new EmergencyContact("  María López  ", new PhoneNumber("70001234"));
        assertThat(contact.name()).isEqualTo("María López");
        assertThat(contact.phoneNumber().value()).isEqualTo("70001234");
    }

    @Test
    void shouldRejectBlankNameOrTooLongNameOrNullPhone() {
        assertThatThrownBy(() -> new EmergencyContact("   ", new PhoneNumber("70001234")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new EmergencyContact("A".repeat(151), new PhoneNumber("70001234")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new EmergencyContact("María López", null))
                .isInstanceOf(NullPointerException.class);
    }
}
