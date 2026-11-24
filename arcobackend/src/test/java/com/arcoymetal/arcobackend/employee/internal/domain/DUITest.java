package com.arcoymetal.arcobackend.employee.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.DUI;

public class DUITest {

    @Test
    void shouldCreateDUIWithNineDigits() {
        DUI dui = new DUI("012345678");

        assertThat(dui.value()).isEqualTo("012345678");
    }

    @Test
    void shouldRemoveHyphenFromDUI() {
        DUI dui = new DUI("01234567-8");

        assertThat(dui.value()).isEqualTo("012345678");
    }

    @Test
    void shouldPreserveLeadingZeros() {
        DUI dui = new DUI("000123456");

        assertThat(dui.value()).isEqualTo("000123456");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatNullPointerException()
                .isThrownBy(() -> new DUI(null))
                .withMessage("DUI cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenValueHasFewerThanNineDigits() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new DUI("12345678"))
                .withMessage(
                        "DUI must contain 9 digits, optionally separated by a hyphen");
    }

    @Test
    void shouldThrowExceptionWhenValueHasMoreThanNineDigits() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new DUI("1234567890"))
                .withMessage(
                        "DUI must contain 9 digits, optionally separated by a hyphen");
    }

    @Test
    void shouldThrowExceptionWhenHyphenIsInWrongPosition() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new DUI("1234-5678"))
                .withMessage(
                        "DUI must contain 9 digits, optionally separated by a hyphen");
    }

    @Test
    void shouldThrowExceptionWhenValueContainsLetters() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new DUI("12345678A"))
                .withMessage(
                        "DUI must contain 9 digits, optionally separated by a hyphen");
    }

}
