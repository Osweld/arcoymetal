package com.arcoymetal.arcobackend.employee.internal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.employee.DistrictId;

public class DistrictIdTest {

    @Test 
    void shouldCreateDistrictIdWithValidValue() {
        DistrictId districtId = new DistrictId("AB-123");
        assertThat(districtId.value()).isEqualTo("AB-123");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatNullPointerException()
        .isThrownBy(() -> new DistrictId(null))
        .withMessage("DistrictId value cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenValueIsBlank() {
        assertThatIllegalArgumentException()
        .isThrownBy(() -> new DistrictId("   "))
        .withMessage("DistrictId value cannot be blank");
    }

    @Test 
    void shouldTrimValueWhenCreatingDistrictId() {
        DistrictId districtId = new DistrictId("  AB-123  ");
        assertThat(districtId.value()).isEqualTo("AB-123");
    }


}
