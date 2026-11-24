package com.arcoymetal.arcobackend.employee.internal.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.Email;

public class EmailTest {

     @ParameterizedTest
    @ValueSource(strings = {
        "german@gmail.com",
        "german.perez@empresa.com.sv",
        "german+test@example.com",
        "user_name@example.com",
        "user-name@example.org"
    })
    void shouldAcceptValidEmails(String value) {
        assertDoesNotThrow(() -> new Email(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "   ",
        "german@@example.com",
        "german@localhost",
        "@example.com",
        "german@",
        ".german@example.com",
        "german.@example.com",
        "german..perez@example.com",
        "german@-example.com",
        "german@example-.com",
        "german@example..com"
    })
    void shouldRejectInvalidEmails(String value) {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Email(value)
        );
    }

    @Test
    void shouldRejectNullEmail() {
        assertThrows(
            NullPointerException.class,
            () -> new Email(null)
        );
    }

    @Test
    void shouldTrimWhitespace() {
        Email email = new Email("  german@example.com  ");

        assertEquals("german@example.com", email.value());
    }

    @Test
    void shouldAcceptEmailWith254Characters() {
        String local = "a".repeat(64);

        String domain = "a".repeat(63) + "."
                + "b".repeat(63) + "."
                + "c".repeat(57) + ".com";

        String value = local + "@" + domain;

        assertEquals(254, value.length());
        assertDoesNotThrow(() -> new Email(value));
    }

    @Test
    void shouldRejectEmailWith255Characters() {
        String local = "a".repeat(64);

        String domain = "a".repeat(63) + "."
                + "b".repeat(63) + "."
                + "c".repeat(58) + ".com";

        String value = local + "@" + domain;

        assertEquals(255, value.length());

        assertThrows(
            IllegalArgumentException.class,
            () -> new Email(value)
        );
    }

    @Test
    void shouldRejectExtremelyLongInput() {
        String value = "a".repeat(10_000) + "@example.com";

        assertThrows(
            IllegalArgumentException.class,
            () -> new Email(value)
        );
    }

}
