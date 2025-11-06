package com.mcqueide.dockertest.interfaces.web;

import com.mcqueide.dockertest.application.service.PersonService;
import com.mcqueide.dockertest.domain.model.Person;
import com.mcqueide.dockertest.interfaces.web.dto.PersonRequest;
import com.mcqueide.dockertest.interfaces.web.dto.PersonResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/people")
public class PersonController {
    private final PersonService service;
    public PersonController(PersonService service) { this.service = service; }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonResponse create(@RequestBody @Valid PersonRequest req) {
        var created = service.create(new Person(null, req.name(), req.birthday(), req.address(), req.phone()));
        return toResponse(created);
    }


    @PutMapping("/{id}")
    public PersonResponse update(@PathVariable UUID id, @RequestBody @Valid PersonRequest req) {
        var updated = service.update(id, new Person(id, req.name(), req.birthday(), req.address(), req.phone()));
        return toResponse(updated);
    }


    @GetMapping("/{id}")
    public PersonResponse get(@PathVariable UUID id) { return toResponse(service.get(id)); }


    @GetMapping
    public List<PersonResponse> list(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size) {
        return service.list(page, size).stream().map(this::toResponse).toList();
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { service.delete(id); }


    private PersonResponse toResponse(Person p) {
        return new PersonResponse(p.id(), p.name(), p.birthday(), p.address(), p.phone());
    }
}