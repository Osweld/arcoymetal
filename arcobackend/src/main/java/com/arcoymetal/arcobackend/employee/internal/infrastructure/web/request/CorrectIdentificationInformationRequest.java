package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CorrectIdentificationInformationRequest(
        @NotBlank @Pattern(regexp = "^(?:\\d{9}|\\d{8}-\\d)$") String dui,
        @Pattern(regexp = "^(?:\\d{14}|\\d{4}-\\d{6}-\\d{3}-\\d)$") String nit,
        @Pattern(regexp = "^(?:\\d{9}|\\d{8}-\\d)$") String isss,
        @Pattern(regexp = "^(?:\\d{9}|\\d{8}-\\d)$") String nup) {

}