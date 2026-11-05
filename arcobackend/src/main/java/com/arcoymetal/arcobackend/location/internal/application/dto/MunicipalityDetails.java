package com.arcoymetal.arcobackend.location.internal.application.dto;

import java.util.List;

public record MunicipalityDetails(
    String id,
    String name,
    List<DistrictDetails> districts
) {

}
