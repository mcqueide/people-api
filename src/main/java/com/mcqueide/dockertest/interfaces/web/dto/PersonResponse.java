package com.mcqueide.dockertest.interfaces.web.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PersonResponse(
    UUID id,
    String name,
    LocalDate birthday,
    String address,
    String phone
){
}
