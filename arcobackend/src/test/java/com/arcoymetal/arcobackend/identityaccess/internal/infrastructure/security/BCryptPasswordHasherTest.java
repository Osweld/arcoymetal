package com.arcoymetal.arcobackend.identityaccess.internal.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.PasswordHash;

class BCryptPasswordHasherTest {

    @Test
    void shouldHashAndVerifyPasswordWithBCrypt() {
        BCryptPasswordHasher hasher = new BCryptPasswordHasher(new BCryptPasswordEncoder(4));

        PasswordHash hash = hasher.hash("Segura123!");

        assertThat(hash.value()).isNotEqualTo("Segura123!");
        assertThat(hash.value()).hasSize(60);
        assertThat(hasher.matches("Segura123!", hash)).isTrue();
        assertThat(hasher.matches("Incorrecta1!", hash)).isFalse();
    }
}
