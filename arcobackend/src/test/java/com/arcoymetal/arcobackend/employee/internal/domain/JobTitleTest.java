package com.arcoymetal.arcobackend.employee.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.employment.JobTitle;

public class JobTitleTest {

    @Test 
    void shouldCreateJobTitleWithValidValue() {
        JobTitle jobTitle = new JobTitle("Software Engineer");
        assertThat(jobTitle.value()).isEqualTo("Software Engineer");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatNullPointerException()
        .isThrownBy(() -> new JobTitle(null))
        .withMessage("JobTitle value cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenValueIsBlank() {
        assertThatIllegalArgumentException()
        .isThrownBy(() -> new JobTitle("   "))
        .withMessage("JobTitle value cannot be blank");
    }

    @Test 
    void shouldTrimValueWhenCreatingJobTitle() {
        JobTitle jobTitle = new JobTitle("  Software Engineer  ");
        assertThat(jobTitle.value()).isEqualTo("Software Engineer");
    }
}
