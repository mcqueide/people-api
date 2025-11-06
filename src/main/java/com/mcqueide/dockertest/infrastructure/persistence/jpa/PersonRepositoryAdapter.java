package com.mcqueide.dockertest.infrastructure.persistence.jpa;

import com.mcqueide.dockertest.domain.model.Person;
import com.mcqueide.dockertest.domain.port.PersonRepositoryPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PersonRepositoryAdapter implements PersonRepositoryPort {
    private final SpringDataPersonRepository jpa;
    private final PersonJpaMapper mapper;

    public PersonRepositoryAdapter(SpringDataPersonRepository jpa, PersonJpaMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Person save(Person person) {
        return mapper.toDomain(jpa.save(mapper.toEntity(person)));
    }

    @Override
    public Optional<Person> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    public List<Person> findAll(int page, int size) {
        return jpa.findAll(PageRequest.of(page, size)).map(mapper::toDomain).toList();
    }
}