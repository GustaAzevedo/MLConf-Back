package com.mlconf.adapters.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConferenceSessionJpaRepository extends JpaRepository<ConferenceSessionJpaEntity, UUID> {
}
