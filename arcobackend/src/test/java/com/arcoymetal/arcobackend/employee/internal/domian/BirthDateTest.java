package com.arcoymetal.arcobackend.employee.internal.domian;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.BirthDate;

public class BirthDateTest {

    private static final ZoneId EL_SALVADOR = ZoneId.of("America/El_Salvador");

    @Test 
    void shouldCreateBirthDateWithValueIsValid(){
        LocalDate date = LocalDate.of(1998, 5, 11);
        BirthDate birthDate = new BirthDate(date);
        assertThat(birthDate.value()).isEqualTo(date);
    }

    @Test
    void shouldAcceptTodayAsBirthDate() {
        LocalDate today = LocalDate.now(EL_SALVADOR);
        BirthDate birthDate = new BirthDate(today);
        assertThat(birthDate.value()).isEqualTo(today);
    }

    @Test
    void shouldAcceptPastDateAsBirthDate(){
        LocalDate yestterday = LocalDate.now(EL_SALVADOR).minusDays(1);
        BirthDate birthDate = new BirthDate(yestterday);
        assertThat(birthDate.value()).isEqualTo(yestterday);
    }
    
    @Test
    void shouldThrowExceptionWhenValueIsInTheFuture() {
        LocalDate tomorrow = LocalDate.now(EL_SALVADOR).plusDays(1);
        assertThatIllegalArgumentException()
        .isThrownBy(() -> new BirthDate(tomorrow))
        .withMessage("BirthDate cannot be in the future");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull(){
        assertThatNullPointerException()
        .isThrownBy(() -> new BirthDate(null))
        .withMessage("BirthDate value cannot be null");
    }

   
}
