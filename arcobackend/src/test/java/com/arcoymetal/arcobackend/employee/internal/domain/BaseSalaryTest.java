package com.arcoymetal.arcobackend.employee.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.BaseSalary;

public class BaseSalaryTest {

    @Test
    void shouldCreateBaseSalaryWhenValueIsValid(){
        BaseSalary baseSalary = new BaseSalary(new BigDecimal("1000.00"));

        assertThat(baseSalary.value())
            .isEqualTo(new java.math.BigDecimal("1000.00"));
    }

    @Test
    void shouldAcceptZeroSalary(){
        BaseSalary baseSalary = new BaseSalary(BigDecimal.ZERO);

        assertThat(baseSalary.value())
            .isEqualByComparingTo("0.00");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatNullPointerException()
            .isThrownBy(() -> new BaseSalary(null))
            .withMessage("Base salary value cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNegative() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new BaseSalary(new BigDecimal("-100.00")))
            .withMessage("Base salary cannot be negative");
    }

    @Test
    void shouldNormalizeValueToTwoDecimalPlaces() {
        BaseSalary salary = new BaseSalary(new BigDecimal("500"));

        assertThat(salary.value().scale()).isEqualTo(2);
        assertThat(salary.value()).isEqualByComparingTo("500.00");
    }

    @Test
    void shouldAcceptValueWithOneDecimalPlace() {
        BaseSalary salary = new BaseSalary(new BigDecimal("500.5"));

        assertThat(salary.value())
            .isEqualTo(new BigDecimal("500.50"));
    }

    @Test
    void shouldThrowExceptionWhenValueRequiresRounding() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> new BaseSalary(new BigDecimal("500.256")));
    }

    @Test
    void shouldAcceptValueWithMoreThanTwoDecimalPlacesWhenExtraDigitsAreZero() {
        BaseSalary salary = new BaseSalary(new BigDecimal("500.2500"));

        assertThat(salary.value())
            .isEqualTo(new BigDecimal("500.25"));
    }
}
