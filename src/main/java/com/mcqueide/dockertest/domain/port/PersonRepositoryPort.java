package com.mcqueide.dockertest.domain.port;

import com.mcqueide.dockertest.domain.model.Person;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonRepositoryPort {
    Person save(Person person);
    Optional<Person> findById(UUID id);
    void deleteById(UUID id);
    List<Person> findAll(int page, int size);
}
