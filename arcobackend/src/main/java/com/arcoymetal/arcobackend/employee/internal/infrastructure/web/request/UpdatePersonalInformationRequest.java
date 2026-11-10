package com.arcoymetal.arcobackend.employee.internal.infrastructure.web.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record UpdatePersonalInformationRequest(
                @NotBlank @Size(min = 1, max = 100) String firstName,
                @NotBlank @Size(min = 1, max = 100) String lastName,
                @NotBlank @Email @Size(min = 1, max = 254) String email,
                @NotBlank @Size(min = 8, max = 8) String phoneNumber,
                @NotBlank @Size(min = 1, max = 150) String addressLine,
                @NotBlank String districtId,
                @NotNull @Past LocalDate birthDate) {

}
