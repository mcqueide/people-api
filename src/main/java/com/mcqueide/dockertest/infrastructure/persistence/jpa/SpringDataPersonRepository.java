package com.mcqueide.dockertest.infrastructure.persistence.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataPersonRepository extends JpaRepository<PersonJpaEntity, UUID> {
    Page<PersonJpaEntity> findAll(Pageable pageable);

}