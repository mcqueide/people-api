package com.mcqueide.dockertest.interfaces.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record PersonRequest(
    @NotBlank String name,
    @Past(message = "Birthday must be in the past") LocalDate birthday,
    String address,
    String phone
){
}
