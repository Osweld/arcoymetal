package com.arcoymetal.arcobackend.employee.internal.domian;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.Address;
import com.arcoymetal.arcobackend.employee.internal.domain.AddressLine;
import com.arcoymetal.arcobackend.employee.internal.domain.DistrictId;

public class AddressTest {

     @Test
    void shouldCreateAddressWhenAllFieldsAreValid() {
        AddressLine addressLine = new AddressLine("Colonia Centro, casa 10");
        DistrictId districtId = new DistrictId("AH0101");

        Address address = new Address(addressLine, districtId);

        assertThat(address.addressLine()).isEqualTo(addressLine);
        assertThat(address.districtId()).isEqualTo(districtId);
    }

    @Test
    void shouldThrowExceptionWhenAddressLineIsNull() {
        DistrictId districtId = new DistrictId("AH0101");

        assertThatNullPointerException()
            .isThrownBy(() -> new Address(null, districtId))
            .withMessage("AddressLine cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenDistrictIdIsNull() {
        AddressLine addressLine = new AddressLine("Colonia Centro, casa 10");

        assertThatNullPointerException()
            .isThrownBy(() -> new Address(addressLine, null))
            .withMessage("DistrictId cannot be null");
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        Address address1 = new Address(
            new AddressLine("Colonia Centro, casa 10"),
            new DistrictId("AH0101")
        );

        Address address2 = new Address(
            new AddressLine("Colonia Centro, casa 10"),
            new DistrictId("AH0101")
        );

        assertThat(address1)
            .isEqualTo(address2)
            .hasSameHashCodeAs(address2);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        Address address1 = new Address(
            new AddressLine("Colonia Centro, casa 10"),
            new DistrictId("AH0101")
        );

        Address address2 = new Address(
            new AddressLine("Colonia Norte, casa 20"),
            new DistrictId("AH0101")
        );

        assertThat(address1).isNotEqualTo(address2);
    }
}
