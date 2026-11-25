package com.arcoymetal.arcobackend.identityaccess.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Email;

class EmailTest {

    @Test
    void shouldCreateAndStripValidEmail() {
        Email email = new Email("  gerencia@arcoymetal.com.sv  ");
        assertThat(email.value()).isEqualTo("gerencia@arcoymetal.com.sv");
    }

    @Test
    void shouldRejectNullEmptyTooLongAndInvalidFormat() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Email(null))
                .withMessage("Email cannot be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Email("   "))
                .withMessage("Email cannot be empty");

        String tooLong = "a".repeat(245) + "@example.com";
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Email(tooLong))
                .withMessageContaining("cannot exceed 254");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Email("not-an-email"))
                .withMessage("Invalid email format");
    }
}
