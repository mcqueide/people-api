package com.mcqueide.dockertest.infrastructure.persistence.jpa;

import com.mcqueide.dockertest.domain.model.Person;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonJpaMapper {
    Person toDomain(PersonJpaEntity e);
    PersonJpaEntity toEntity(Person p);
}