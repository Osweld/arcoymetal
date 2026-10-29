package com.arcoymetal.arcobackend.employee.internal.domian;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

public class NUPTest {

    @Test
    void shouldCreateNUPWithNineDigits() {
        NUP nup = new NUP("012345678");

        assertThat(nup.value()).isEqualTo("012345678");
    }

    @Test
    void shouldRemoveHyphenFromNUP() {
        NUP nup = new NUP("01234567-8");

        assertThat(nup.value()).isEqualTo("012345678");
    }

    @Test
    void shouldPreserveLeadingZeros() {
        NUP nup = new NUP("000123456");

        assertThat(nup.value()).isEqualTo("000123456");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatNullPointerException()
                .isThrownBy(() -> new NUP(null))
                .withMessage("NUP cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenValueHasFewerThanNineDigits() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new NUP("12345678"))
                .withMessage(
                        "NUP must contain 9 digits, optionally separated by hyphens");
    }

    @Test
    void shouldThrowExceptionWhenValueHasMoreThanNineDigits() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new NUP("1234567890"))
                .withMessage(
                        "NUP must contain 9 digits, optionally separated by hyphens");
    }

    @Test
    void shouldThrowExceptionWhenHyphenIsInWrongPosition() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new NUP("1234-5678"))
                .withMessage(
                        "NUP must contain 9 digits, optionally separated by hyphens");
    }

    @Test
    void shouldThrowExceptionWhenValueContainsLetters() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new NUP("12345678A"))
                .withMessage(
                        "NUP must contain 9 digits, optionally separated by hyphens");
    }

}
