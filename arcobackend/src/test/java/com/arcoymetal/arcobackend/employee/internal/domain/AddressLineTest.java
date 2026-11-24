package com.arcoymetal.arcobackend.employee.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.AddressLine;

public class AddressLineTest {

    @Test
    void shouldCreateAddressLineWhenValueIsValid() {
        AddressLine addressLine = new AddressLine("Colonia Centro, casa 10");

        assertThat(addressLine.value())
            .isEqualTo("Colonia Centro, casa 10");
    }

    @Test
    void shouldRemoveLeadingAndTrailingWhitespace() {
        AddressLine addressLine =
            new AddressLine("  Colonia Centro, casa 10  ");

        assertThat(addressLine.value())
            .isEqualTo("Colonia Centro, casa 10");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatNullPointerException()
            .isThrownBy(() -> new AddressLine(null))
            .withMessage("AddressLine value cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenValueIsEmpty() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new AddressLine(""))
            .withMessage("AddressLine value cannot be blank");
    }

    @Test
    void shouldThrowExceptionWhenValueContainsOnlyWhitespace() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new AddressLine("   "))
            .withMessage("AddressLine value cannot be blank");
    }

    @Test
    void shouldAcceptValueWhenLengthIsExactlyMaxLength() {
        String value = "A".repeat(150);

        AddressLine addressLine = new AddressLine(value);

        assertThat(addressLine.value())
            .hasSize(150)
            .isEqualTo(value);
    }

    @Test
    void shouldThrowExceptionWhenValueExceedsMaxLength() {
        String value = "A".repeat(151);

        assertThatIllegalArgumentException()
            .isThrownBy(() -> new AddressLine(value))
            .withMessage("AddressLine cannot exceed 150 characters");
    }
}
