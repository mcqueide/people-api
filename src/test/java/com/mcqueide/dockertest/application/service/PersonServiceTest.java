package com.mcqueide.dockertest.application.service;

import com.mcqueide.dockertest.domain.model.Person;
import com.mcqueide.dockertest.domain.port.PersonRepositoryPort;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PersonServiceTest {
    @Test
    void createAssignsIdAndPersists() {
        var repo = mock(PersonRepositoryPort.class);
        var svc = new PersonService(repo);

        var toCreate = new Person(null, "Ada Lovelace", LocalDate.of(1815,12,10), "London", "+44 123");
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var saved = svc.create(toCreate);
        assertNotNull(saved.id());
        assertEquals("Ada Lovelace", saved.name());
        verify(repo, times(1)).save(any());
    }

    @Test
    void updateRequiresExisting() {
        var repo = mock(PersonRepositoryPort.class);
        var svc = new PersonService(repo);
        var id = UUID.randomUUID();

        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                svc.update(id, new Person(id, "New Name", null, null, null)));
    }
}