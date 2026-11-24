package com.arcoymetal.arcobackend.employee.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.ISSS;

public class ISSSTest {

    @Test
    void shouldCreateISSSWithNineDigits() {
        ISSS isss = new ISSS("012345678");

        assertThat(isss.value()).isEqualTo("012345678");
    }

    @Test
    void shouldRemoveHyphenFromISSS() {
        ISSS isss = new ISSS("01234567-8");

        assertThat(isss.value()).isEqualTo("012345678");
    }

    @Test
    void shouldPreserveLeadingZeros() {
        ISSS isss = new ISSS("000123456");

        assertThat(isss.value()).isEqualTo("000123456");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatNullPointerException()
                .isThrownBy(() -> new ISSS(null))
                .withMessage("ISSS cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenValueHasFewerThanNineDigits() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new ISSS("12345678"))
                .withMessage(
                        "ISSS must contain 9 digits, optionally separated by a hyphen");
    }

    @Test
    void shouldThrowExceptionWhenValueHasMoreThanNineDigits() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new ISSS("1234567890"))
                .withMessage(
                        "ISSS must contain 9 digits, optionally separated by a hyphen");
    }

    @Test
    void shouldThrowExceptionWhenHyphenIsInWrongPosition() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new ISSS("1234-5678"))
                .withMessage(
                        "ISSS must contain 9 digits, optionally separated by a hyphen");
    }

    @Test
    void shouldThrowExceptionWhenValueContainsLetters() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new ISSS("12345678A"))
                .withMessage(
                        "ISSS must contain 9 digits, optionally separated by a hyphen");
    }
}
