package com.arcoymetal.arcobackend.employee.internal.domian;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.NIT;

public class NITTest {

     @Test
    void shouldCreateNITWithFourteenDigits() {
        NIT nit = new NIT("1234-567890-123-4");

        assertThat(nit.value()).isEqualTo("12345678901234");
    }

    @Test
    void shouldRemoveHyphenFromNIT() {
        NIT nit = new NIT("12345678901234");

        assertThat(nit.value()).isEqualTo("12345678901234");
    }

    @Test
    void shouldPreserveLeadingZeros() {
        NIT nit = new NIT("00012345678901");

        assertThat(nit.value()).isEqualTo("00012345678901");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatNullPointerException()
                .isThrownBy(() -> new NIT(null))
                .withMessage("NIT cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenValueHasFewerThanNineDigits() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new NIT("12345678"))
                .withMessage(
                        "NIT must contain 14 digits, optionally separated by hyphens");
    }

    @Test
    void shouldThrowExceptionWhenValueHasMoreThanNineDigits() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new NIT("123456789012345"))
                .withMessage(
                        "NIT must contain 14 digits, optionally separated by hyphens");
    }

    @Test
    void shouldThrowExceptionWhenHyphenIsInWrongPosition() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new NIT("1234-5678"))
                .withMessage(
                        "NIT must contain 14 digits, optionally separated by hyphens");
    }

    @Test
    void shouldThrowExceptionWhenValueContainsLetters() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new NIT("12345678A"))
                .withMessage(
                        "NIT must contain 14 digits, optionally separated by hyphens");
    }
}
