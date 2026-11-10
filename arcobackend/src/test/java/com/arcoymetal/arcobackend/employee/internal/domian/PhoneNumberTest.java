package com.arcoymetal.arcobackend.employee.internal.domian;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.PhoneNumber;

public class PhoneNumberTest {

    @Test
    void shouldCreatePhoneNumberWithEightDigits() {
        PhoneNumber phoneNumber = new PhoneNumber("12345678");

        assertThat(phoneNumber.value()).isEqualTo("12345678");
    }

    @Test
    void shouldPreserveLeadingZeros() {
        PhoneNumber phoneNumber = new PhoneNumber("00123456");

        assertThat(phoneNumber.value()).isEqualTo("00123456");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatNullPointerException()
                .isThrownBy(() -> new PhoneNumber(null))
                .withMessage("Phone number cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenValueHasFewerThanNineDigits() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PhoneNumber("1234567"))
                .withMessage(
                        "Phone number must be 8 digits");
    }

    @Test
    void shouldThrowExceptionWhenValueHasMoreThanNineDigits() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PhoneNumber("123456789"))
                .withMessage(
                        "Phone number must be 8 digits");
    }


    @Test
    void shouldThrowExceptionWhenValueContainsLetters() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PhoneNumber("12345678A"))
                .withMessage(
                        "Phone number must be 8 digits");
    }
}
