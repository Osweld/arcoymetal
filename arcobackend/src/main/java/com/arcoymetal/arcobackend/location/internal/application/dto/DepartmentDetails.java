package com.arcoymetal.arcobackend.location.internal.application.dto;

import java.util.List;

public record DepartmentDetails(
    String id,
    String name,
    List<MunicipalityDetails> municipalities
) {

}
