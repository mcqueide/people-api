package com.mcqueide.dockertest.application.service;

import com.mcqueide.dockertest.domain.model.Person;
import com.mcqueide.dockertest.domain.port.PersonRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PersonService {

    private final PersonRepositoryPort repo;

    public PersonService(PersonRepositoryPort repo) {
        this.repo = repo;
    }

    public Person create(Person p) {
        return repo.save(new Person(UUID.randomUUID(), p.name(), p.birthday(), p.address(), p.phone()));
    }

    public Person update(UUID id, Person p) {
        repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Person not found: " + id));
        return repo.save(new Person(id, p.name(), p.birthday(), p.address(), p.phone()));
    }

    public Person get(UUID id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Person not found: " + id));
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }

    public List<Person> list(int page, int size) {
        return repo.findAll(page, size);
    }
}
