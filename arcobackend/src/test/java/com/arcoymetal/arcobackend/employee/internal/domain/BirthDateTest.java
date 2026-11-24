package com.arcoymetal.arcobackend.employee.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.BirthDate;

class BirthDateTest {

    @Test
    void shouldCreateBirthDateWhenValueIsValid() {
        LocalDate date = LocalDate.of(1998, 5, 11);
        BirthDate birthDate = new BirthDate(date);
        assertThat(birthDate.value()).isEqualTo(date);
    }

    @Test
    void shouldAcceptBoundaryYear1920() {
        LocalDate boundary = LocalDate.of(1920, 1, 1);
        BirthDate birthDate = new BirthDate(boundary);
        assertThat(birthDate.value()).isEqualTo(boundary);
    }

    @Test
    void shouldThrowExceptionWhenBirthDateIsBefore1920() {
        LocalDate tooOld = LocalDate.of(1919, 12, 31);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new BirthDate(tooOld))
                .withMessage("Birth date is unreasonably old");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatNullPointerException()
                .isThrownBy(() -> new BirthDate(null))
                .withMessage("Birth date cannot be null");
    }

    @Test
    void shouldCalculateAgeAtReferenceDateAndRejectInvalidReferenceDate() {
        BirthDate birthDate = new BirthDate(LocalDate.of(2000, 6, 15));

        assertThat(birthDate.ageAt(LocalDate.of(2018, 6, 14))).isEqualTo(17);
        assertThat(birthDate.ageAt(LocalDate.of(2018, 6, 15))).isEqualTo(18);

        assertThatNullPointerException()
                .isThrownBy(() -> birthDate.ageAt(null))
                .withMessage("Reference date cannot be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> birthDate.ageAt(LocalDate.of(2000, 6, 14)))
                .withMessage("Reference date cannot be before birth date");
    }
}
