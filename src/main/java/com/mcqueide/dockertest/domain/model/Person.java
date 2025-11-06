package com.mcqueide.dockertest.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public record Person(
        UUID id,
        String name,
        LocalDate birthday,
        String address,
        String phone
) {

}