package com.arcoymetal.arcobackend.employee.internal.domain.employee;

import java.util.Objects;

public record Address(AddressLine addressLine, DistrictId districtId) {

    public Address {
      Objects.requireNonNull(addressLine, "AddressLine cannot be null");
      Objects.requireNonNull(districtId, "DistrictId cannot be null");
      
    }

}
