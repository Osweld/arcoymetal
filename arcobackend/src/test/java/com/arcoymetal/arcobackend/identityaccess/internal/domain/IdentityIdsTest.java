package com.arcoymetal.arcobackend.identityaccess.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.EmployeeId;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.UserId;

class IdentityIdsTest {

    @Test
    void shouldCreateAndGenerateUserIdAndEmployeeId() {
        UUID uuid = UUID.randomUUID();

        assertThat(new UserId(uuid).value()).isEqualTo(uuid);
        assertThat(UserId.generate().value()).isNotNull();

        assertThat(new EmployeeId(uuid).value()).isEqualTo(uuid);
        assertThat(EmployeeId.generate().value()).isNotNull();
    }

    @Test
    void shouldRejectNullValues() {
        assertThatNullPointerException()
                .isThrownBy(() -> new UserId(null))
                .withMessage("UserId value cannot be null");

        assertThatNullPointerException()
                .isThrownBy(() -> new EmployeeId(null))
                .withMessage("EmployeeId value cannot be null");
    }
}
