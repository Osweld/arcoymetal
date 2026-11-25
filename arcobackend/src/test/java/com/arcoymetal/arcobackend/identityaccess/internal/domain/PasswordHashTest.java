package com.arcoymetal.arcobackend.identityaccess.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.PasswordHash;

class PasswordHashTest {

    @Test
    void shouldAcceptValidLengthsBetween60And100() {
        String min = "a".repeat(60);
        String max = "b".repeat(100);

        assertThat(new PasswordHash(min).value()).isEqualTo(min);
        assertThat(new PasswordHash(max).value()).isEqualTo(max);
    }

    @Test
    void shouldRejectNullTooShortAndTooLong() {
        assertThatNullPointerException()
                .isThrownBy(() -> new PasswordHash(null))
                .withMessage("Password hash cannot be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PasswordHash("a".repeat(59)))
                .withMessageContaining("between 60 and 100");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PasswordHash("a".repeat(101)))
                .withMessageContaining("between 60 and 100");
    }
}
